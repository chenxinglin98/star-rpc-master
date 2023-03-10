package com.star.remoting.transport.netty.client;

import com.star.enums.CompressTypeEnum;
import com.star.enums.SerializationTypeEnum;
import com.star.extension.ExtensionLoader;
import com.star.factory.SingletonFactory;
import com.star.registry.ServiceDiscovery;
import com.star.remoting.constants.RpcConstants;
import com.star.remoting.entity.RpcMessage;
import com.star.remoting.entity.RpcRequest;
import com.star.remoting.entity.RpcResponse;
import com.star.remoting.transport.RpcRequestTransport;
import com.star.remoting.transport.netty.codec.RpcMessageDecoder;
import com.star.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-04 23:33
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyRpcClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //  The timeout period of the connection.
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent 自带心跳机制
                        //https://blog.csdn.net/u013967175/article/details/78591810
                        channel.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        channel.pipeline().addLast(new RpcMessageEncoder());
                        channel.pipeline().addLast(new RpcMessageDecoder());
                        channel.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }

    /**
     * connect server and get the channel ,so that you can send rpc message to server
     *
     * @param inetSocketAddress server address
     * @return the channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                // 如果任务没有完成，返回的值设置为给定值future.channel()
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        //返回结果
        return completableFuture.get();
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        // build return value
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // get server address
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        // get  server address related channel
        Channel channel = getChannel(inetSocketAddress);
        if(channel.isActive()){
            unprocessedRequests.put(rpcRequest.getRequestId(),resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    .codec(SerializationTypeEnum.KYRO.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener)future -> {
                if(future.isSuccess()){
                    log.info("client send message: [{}]", rpcMessage);
                }else{
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        }else {
            throw new IllegalStateException();
        }
        //返回异步对象,这个对象也加入了unprocessedRequests Map中
        //服务端发送Response回来到NettyRpcClientHandler里会给resultFuture设置结果,然后想要的时候调用get就行
        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        //看看存储的是否已经有这个已经连接好的channel了
        Channel channel = channelProvider.get(inetSocketAddress);
        //没有的话连接再获得channel
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }
}

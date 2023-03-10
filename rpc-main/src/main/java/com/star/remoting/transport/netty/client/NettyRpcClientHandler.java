package com.star.remoting.transport.netty.client;

import com.star.enums.CompressTypeEnum;
import com.star.enums.SerializationTypeEnum;
import com.star.extension.SPI;
import com.star.factory.SingletonFactory;
import com.star.remoting.constants.RpcConstants;
import com.star.remoting.entity.RpcMessage;
import com.star.remoting.entity.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-07 22:10
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;
    private final NettyRpcClient nettyRpcClient;

    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.nettyRpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage message = (RpcMessage) msg;
                byte messageType = message.getMessageType();
                //Client只会收到Response
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart [{}]", message.getData());
                } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) message.getData();
                    //说明完成了Response的RequestId对应的Request
                    //移除UNPROCESSED_RESPONSE_FUTURES里面未被处理的请求
                    //如果他的线程没有完成的话,调用complete获得默认的返回的值设置为给定值(rpcResponse)
                    //其实只是为了记录而记录,并不让这个线程具体做什么,也就是不要求得到线程的处理结果
                    //只想通过complete设置rpcResponse,同时在未处理请求的map种移除这个entry
                    //然后之后就可以通过这个future取rpcResponse了,
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            //不管是否处理完都释放缓存,没处理的交给继续taskQueue处理
            ReferenceCountUtil.release(msg);
        }
    }

    //Netty心跳机制相关,保証客戸端和服努端的達接不被断掉，避免重達。结合IdleStateHandler
    //作为第一个Handler,出入负责
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE){
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                //远程地址对应的本地channel ,为什么不直接用当前的ctx.channel()?
                //当前的channel可能关闭了,所以通过这个方法里doConnect再注册一下?
                Channel channel = nettyRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setData(RpcConstants.PING);
                //如果CLOSE_ON_FAILURE就关闭了channel了
                //channel.writeAndFlush不是从当前Handler开始，而是从末尾的outboundhandler开始的
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Called when an exception occurs in processing a client message
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}

package com.star.remoting.transport.netty.server;

import com.star.enums.CompressTypeEnum;
import com.star.enums.RpcResponseCodeEnum;
import com.star.enums.SerializationTypeEnum;
import com.star.factory.SingletonFactory;
import com.star.remoting.constants.RpcConstants;
import com.star.remoting.entity.RpcMessage;
import com.star.remoting.entity.RpcRequest;
import com.star.remoting.entity.RpcResponse;
import com.star.remoting.handler.RpcRequestHandler;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * Customize the ChannelHandler of the server to process the data sent by the client.
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ，{@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf ，避免可能导致的内存泄露问题。详见《Netty进阶之路 跟着案例学 Netty》
 * @author: 陈星霖
 * @date: 2023-02-09 00:27
 */
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try {
            if(msg instanceof RpcMessage){
                log.info("server receive msg: [{}] ", msg);
                RpcMessage rpcRequestMessage= (RpcMessage)msg;
                RpcMessage rpcResponseMessage=new RpcMessage();
                rpcResponseMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                rpcResponseMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                if(rpcRequestMessage.getMessageType()==RpcConstants.HEARTBEAT_REQUEST_TYPE){
                    rpcResponseMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcResponseMessage.setData(RpcConstants.PONG);
                }else{
                    RpcRequest rpcRequest = (RpcRequest) rpcRequestMessage.getData();
                    // Execute the target method (the method the client needs to execute) and return the method result
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcResponseMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    if(ctx.channel().isActive()&&ctx.channel().isWritable()){
                        RpcResponse<Object> rpcResponse = RpcResponse.RpcSuccess(result,rpcRequest.getRequestId());
                        rpcResponseMessage.setData(rpcResponse);
                    }else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        rpcResponseMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcResponseMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}

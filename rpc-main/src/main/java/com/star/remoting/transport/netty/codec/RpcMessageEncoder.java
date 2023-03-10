package com.star.remoting.transport.netty.codec;

import com.star.compress.Compress;
import com.star.enums.CompressTypeEnum;
import com.star.enums.SerializationTypeEnum;
import com.star.extension.ExtensionLoader;
import com.star.remoting.constants.RpcConstants;
import com.star.remoting.entity.RpcMessage;
import com.star.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: custom protocol decoder
 * 0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 * +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 * |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 * +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 * |                                                                                                       |
 * |                                         body                                                          |
 * |                                                                                                       |
 * |                                        ... ...                                                        |
 * +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）    4B full length（消息长度）    1B messageType（消息类型）
 * 1B  codec（序列化类型）    1B compress（压缩类型)   4B requestId（请求的Id）
 * body（object类型数据）
 * @author: 陈星霖
 * @date: 2023-02-05 00:27
 */
//上面跑的就是字节流 所以转为字节流后 解码使用任何解码器(从byte转回去)都可以
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) {
        try {
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RpcConstants.VERSION);
            //预留4个字节写消息长度
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            byteBuf.writeByte(rpcMessage.getCodec());
            //为什么不直接使用RpcMessage里面的getCompress和getRequestId呢 -getCompress可以用
            byteBuf.writeByte(CompressTypeEnum.GZIP.getCode());
            //byteBuf.writeByte(rpcMessage.getCompress());
            //好像不是RpcRequest的RequestId(因为传的是RpcMessage) 也就是RpcMessage的Id,这时的rpcMessage没有RequestId
            // 下面一行发送了一个Id,接收方会收到后组装成一个rpcMessage
            //我改成了MessageId
            byteBuf.writeInt(ATOMIC_INTEGER.getAndIncrement());
            byte[] bodyBytes = null;
            //头长16 见上图
            int fullLength = RpcConstants.HEAD_LENGTH;
            //这里是netty无关RpcResponse和RpcRequest,他们都只是RpcMessage里面的数据
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
                //serialize rpcMessage.data(rpcRequest|rpcResponse)
                bodyBytes = serializer.serialize(rpcMessage.getData());
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength+=bodyBytes.length;
            }
            if(bodyBytes!=null){
                byteBuf.writeBytes(bodyBytes);
            }
            int writeIndex = byteBuf.writerIndex();
            //写消息长度
            byteBuf.writerIndex(writeIndex-fullLength+RpcConstants.MAGIC_NUMBER.length+1);
            //一个Int为4B
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }
    }
}

package com.star.remoting.entity;


import lombok.*;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-05 00:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * rpc message type
     */
    private byte messageType;
    /**
     * serialization type
     */
    private byte codec;
    /**
     * compress type
     */
    private byte compress;
    /**
     * message id
     */
    private int messageId;
    /**
     * request data
     */
    private Object data;

}

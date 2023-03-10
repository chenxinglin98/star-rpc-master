package com.star.remoting.transport;

import com.star.extension.SPI;
import com.star.remoting.entity.RpcRequest;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-01 23:36
 */
@SPI
public interface RpcRequestTransport {
    Object sendRequest(RpcRequest rpcRequest);
}

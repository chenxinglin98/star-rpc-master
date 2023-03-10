package com.star.provider;

import com.star.config.RpcServiceConfig;
import com.star.extension.SPI;

/**
 * @description: store and provide service object.
 * @author: 陈星霖
 * @date: 2023-02-03 21:50
 */
@SPI
public interface ServiceProvider {

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void publishService(RpcServiceConfig rpcServiceConfig);
}

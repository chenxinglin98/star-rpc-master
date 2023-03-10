package com.star.registry;

import com.star.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-02 17:27
 */
@SPI
public interface ServiceRegistration {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}

package com.star.registry;

import com.star.extension.SPI;
import com.star.remoting.entity.RpcRequest;

import java.net.InetSocketAddress;
@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}

package com.star;

import com.star.config.RpcServiceConfig;
import com.star.remoting.transport.socket.SocketRpcServer;
import com.star.serviceimpl.AnimalServiceImpl;
import com.star.services.AnimalService;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-10 23:16
 */
public class SocketServerMain {
    public static void main(String[] args) {
        AnimalService helloService = new AnimalServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(helloService);
        socketRpcServer.registerService(rpcServiceConfig);
        socketRpcServer.start();
    }
}

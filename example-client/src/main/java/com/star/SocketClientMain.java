package com.star;

import com.star.config.RpcServiceConfig;
import com.star.entity.Animal;
import com.star.proxy.RpcClientProxy;
import com.star.remoting.transport.RpcRequestTransport;
import com.star.remoting.transport.socket.SocketRpcClient;
import com.star.services.AnimalService;

/**
 * @description: '
 * @author: 陈星霖
 * @date: 2023-02-10 23:06
 */
public class SocketClientMain {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        AnimalService animalService = rpcClientProxy.getProxy(AnimalService.class);
        String introduce = animalService.introduce(new Animal("pig", 20));
        System.out.println(introduce);
    }
}

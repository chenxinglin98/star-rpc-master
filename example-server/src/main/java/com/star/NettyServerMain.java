package com.star;

import com.star.annotation.RpcScan;
import com.star.config.RpcServiceConfig;
import com.star.remoting.transport.netty.server.NettyRpcServer;
import com.star.serviceimpl.AnimalServiceImpl2;
import com.star.services.AnimalService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.star"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // Register service manually 手动注册服务
        AnimalService animalService2 = new AnimalServiceImpl2();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2").version("version2").service(animalService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
package com.star.remoting.transport.socket;

import com.star.concurrent.threadpool.utils.ThreadPoolFactoryUtil;
import com.star.config.CustomShutdownHook;
import com.star.config.RpcServiceConfig;
import com.star.factory.SingletonFactory;
import com.star.provider.ServiceProvider;
import com.star.provider.zookeeper.ZkServiceProviderImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-01 23:40
 */

@AllArgsConstructor
@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;
    public static final int PORT = 9998;

    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(host, PORT));
            //注册个钩子线程,在JVM停止后执行收尾工作
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcServerHandler(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("server start-ing exception occurred" + e);
        }
    }
}

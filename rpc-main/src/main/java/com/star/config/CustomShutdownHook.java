package com.star.config;

import com.star.concurrent.threadpool.utils.ThreadPoolFactoryUtil;
import com.star.registry.zookeeper.Utils.CuratorUtils;
import com.star.remoting.transport.socket.SocketRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @description: When the server  is closed, do something such as unregister all services 给当前主线程注册个钩子线程,在JVM停止后执行收尾工作
 * @author: 陈星霖
 * @date: 2023-02-04 17:20
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), SocketRpcServer.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException e) {
                log.error("clear server things failed" + e);
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}

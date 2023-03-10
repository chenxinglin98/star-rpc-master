package com.star.registry.zookeeper;

import com.star.registry.zookeeper.Utils.CuratorUtils;
import com.star.registry.ServiceRegistration;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-02 19:19
 */
public class ZKServiceRegistrationImpl implements ServiceRegistration {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}

package com.star.registry.zookeeper;

import com.star.Utils.CollectionUtil;
import com.star.enums.RpcErrorMessageEnum;
import com.star.exception.RpcException;
import com.star.extension.ExtensionLoader;
import com.star.loadbalance.LoadBalance;
import com.star.loadbalance.loadbalancer.ConsistentHashLoadBalance;
import com.star.registry.ServiceDiscovery;
import com.star.registry.zookeeper.Utils.CuratorUtils;
import com.star.remoting.entity.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.checkerframework.checker.index.qual.SameLen;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-02 19:14
 */
@Slf4j
public class ZKServiceDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZKServiceDiscoveryImpl() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        //通过负载均衡算法选择一个ip
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // load balancing
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}

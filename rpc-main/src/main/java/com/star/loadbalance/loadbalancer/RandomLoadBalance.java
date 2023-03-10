package com.star.loadbalance.loadbalancer;

import com.star.loadbalance.AbstractLoadBalance;
import com.star.remoting.entity.RpcRequest;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-03 19:34
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        return serviceAddresses.get(ThreadLocalRandom.current().nextInt(serviceAddresses.size()));
    }
}

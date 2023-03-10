package com.star.loadbalance;

import com.star.Utils.CollectionUtil;
import com.star.remoting.entity.RpcRequest;

import java.util.List;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-03 19:29
 */
public abstract class AbstractLoadBalance implements LoadBalance{
    //对接口的进一步封装过滤
    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        if(CollectionUtil.isEmpty(serviceUrlList)){
            return null;
        }
        if(serviceUrlList.size()==1){
            return serviceUrlList.get(0);
        }
        return doSelect(serviceUrlList,rpcRequest);
    }
    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);
}

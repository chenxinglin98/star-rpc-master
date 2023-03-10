package com.star.remoting.transport.netty.client;

import com.star.remoting.entity.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 用于存放未被服务端处理的请求
 * @author: 陈星霖
 * @date: 2023-02-08 00:10
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>>  UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();
    //在声明的时候future所对应的线就已经运行了
    public void put(String requestId,CompletableFuture<RpcResponse<Object>> future){
        UNPROCESSED_RESPONSE_FUTURES.put(requestId,future);
    }
    public void complete(RpcResponse<Object> rpcResponse) {
        //从map中取出并移除这个CompletableFuture,然后让他去complete
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestID());
        if (null!=future){
            // 如果任务没有完成，返回的值设置为给定值rpcResponse,完成了就不管了
            future.complete(rpcResponse);
        }else{
            throw new IllegalStateException();
        }
    }
}

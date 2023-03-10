package com.star.remoting.handler;

import com.star.provider.ServiceProvider;
import com.star.provider.zookeeper.ZkServiceProviderImpl;
import com.star.enums.RpcErrorMessageEnum;
import com.star.exception.RpcException;
import com.star.factory.SingletonFactory;
import com.star.remoting.entity.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-04 17:17
 */
@Slf4j
public class RpcRequestHandler {
    public final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }
    /**
     * Processing rpcRequest: call the corresponding method, and then return the method
     */
    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest,service);
    }
    /**
     * get method execution results
     *
     * @param rpcRequest client request
     * @param service    service object
     * @return the result of the target method execution
     */
    public Object invokeTargetMethod(RpcRequest rpcRequest,Object service){
        Object result;
        try {
            //反射获取method,给出通过方法名和方法参数找找method
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            result = method.invoke(service,rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_METHOD_INVOCATION_FAILURE.getMessage(),e);
        }
        return result;
    }
}

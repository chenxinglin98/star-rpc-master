package com.star.remoting.transport.socket;

import com.star.exception.RpcException;
import com.star.extension.ExtensionLoader;
import com.star.registry.ServiceDiscovery;
import com.star.remoting.entity.RpcRequest;
import com.star.remoting.transport.RpcRequestTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-01 23:38
 */
@Slf4j
@AllArgsConstructor
public class SocketRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    public SocketRpcClient(){
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(rpcRequest);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject();
        } catch (IOException |ClassNotFoundException e) {
            throw new RpcException("调用失败",e);
        }
    }
}

package com.star.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    //通过配置以下值,来装载property配置文件
    RPC_CONFIG_PATH("rpc.properties"),
    //通过以下配置去property中找到rpc.zookeeper.address对应的属性
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;

}

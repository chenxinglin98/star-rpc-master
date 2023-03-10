package com.star.remoting.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-01-28 23:42
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -5288720253801650204L;
    private String requestId;
    //?如果对方并没有实现接口怎么办?
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] parameterTypes;
    private String version;
    //为了区分可能出现的不同接口的情况
    private String group;

    //拼装rpc服务名
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}

package com.star.remoting.entity;

import com.star.enums.RpcResponseCodeEnum;
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
@Setter
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 1715146351320130576L;
    private String requestID;
    private String message;
    private Integer code;
    private T data;

    //返回两种相应的RpcResponse
    public static <T> RpcResponse<T> RpcSuccess(T data,String requestID){
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestID(requestID);
        if(data!=null){
            response.setData(data);
        }
        return response;
    }
    //failed可能以后有多种可能,所以为了可扩展性,交给调用他的方法来定义传递信息
    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }
}

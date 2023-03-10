package com.star.exception;

import com.star.enums.RpcErrorMessageEnum;


/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-02 19:14
 */
public class RpcException extends RuntimeException {


    private static final long serialVersionUID = 4139827659553315720L;

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum,String detail) {
        super(rpcErrorMessageEnum.getMessage()+detail);
    }



}

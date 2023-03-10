package com.star.exception;


/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-01-09 23:30
 */
public class SerializeException extends RuntimeException{


    private static final long serialVersionUID = 1867487546331364322L;

    public SerializeException(String message) {
        super(message);
    }
}

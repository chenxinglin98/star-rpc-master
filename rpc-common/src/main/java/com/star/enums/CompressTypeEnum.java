package com.star.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: netty消息压缩方式
 * @author: 陈星霖
 * @date: 2023-02-02 19:14
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    //静态工具方法,通过code得到name 适用于自定义网络协议
    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}

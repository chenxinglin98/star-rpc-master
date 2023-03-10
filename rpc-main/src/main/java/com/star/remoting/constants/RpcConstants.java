package com.star.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @description: https://www.cnblogs.com/caoweixiong/p/14663492.html 自定义传输协议相关常量
 * @author: 陈星霖
 * @date: 2023-02-05 12:22
 */
public class RpcConstants {


    /**
     * Magic number. Verify RpcMessage
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    //version information
    public static final byte VERSION = 1;
    //4B full length（消息长度）即 4*8 = 32bit 能表达即2^32B总长度,但是我们限制为MAX_FRAME_LENGTH 2^23B
    //也是头长度16Byte,如果frame的长度小于TOTAL_LENGTH那么说明后面的body为空
    //编码的时候写一个Int就好了
    public static final byte TOTAL_LENGTH = 16;
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    //头长度16Byte 前9为协议内容,后面全部为RpcMessage对象的一部分(指非body部分),body为RpcMessage对象的body
    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}

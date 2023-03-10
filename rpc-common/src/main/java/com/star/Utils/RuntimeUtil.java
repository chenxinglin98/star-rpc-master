package com.star.Utils;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-09 18:04
 */
public class RuntimeUtil {
    /**
     * 获取CPU的核心数
     *
     * @return cpu的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}

package com.star.Utils;

import java.util.Collection;

/**
 * @description:  集合工具类
 * @author: 陈星霖
 * @date: 2023-02-03 19:29
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

}

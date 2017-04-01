package com.yisheng.framework.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by easom on 2017/3/26.
 * 数组工具类，被IocHelper调用
 */
public final class ArrayUtil {
    //判断数组是否非空
    public static boolean isNotEmpty(Object[] array){
        return !ArrayUtils.isEmpty(array);
    }
    //判断数组是否为空
    public static boolean isEmpty(Object[]array){
        return ArrayUtils.isEmpty(array);
    }
}

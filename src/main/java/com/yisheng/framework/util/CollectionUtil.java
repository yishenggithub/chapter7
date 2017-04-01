package com.yisheng.framework.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by easom on 2017/3/12.
 * 集合类工具
 */
public class CollectionUtil {
    //判断Collection是否为空
    public static boolean isEmpty(Collection<?> collection){
        return CollectionUtils.isEmpty(collection);
    }
    //判断Collection是否非空
    public  static boolean isNotEmpty(Collection<?> collection){
        return !isEmpty(collection);
    }

    //判断Map是否为空
    public  static  boolean isEmpty(Map<?,?> map){
        return MapUtils.isEmpty(map);
    }
    //判断Map是否非空
    public static  boolean isNotEmpty(Map<?,?> map){
        return !isEmpty(map);
    }
}

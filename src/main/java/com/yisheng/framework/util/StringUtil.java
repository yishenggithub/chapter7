package com.yisheng.framework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by easom on 2017/3/7.
 */
//字符串工具
public final class StringUtil {

        public static  boolean isEmpty(String str){
            if(str!=null){
                str=str.trim(); //起始和结尾的空格都被删除了
            }
            return StringUtils.isEmpty(str);
        }
        public static  boolean isNotEmpty(String str){
            return !isEmpty(str);
        }
}

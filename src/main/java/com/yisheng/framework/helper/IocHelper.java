package com.yisheng.framework.helper;

import com.yisheng.framework.annotation.Inject;
import com.yisheng.framework.util.ArrayUtil;
import com.yisheng.framework.util.CollectionUtil;
import com.yisheng.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Created by easom on 2017/3/26.
 */
public final class IocHelper {
    static {
        //获取所有Bean类与Bean实例之间的映射关系（简称BeanMap）
        Map<Class<?>,Object>beanMap=BeanHelper.getBeanMap();
        if(CollectionUtil.isNotEmpty(beanMap)){
            //遍历BeanMap
            for(Map.Entry<Class<?>,Object>beanEntry:beanMap.entrySet()){
                //从BeanMap中获取Bean类与Bean实例
                Class<?>beanClass=beanEntry.getKey();
                Object beanInstance=beanEntry.getValue();
                //获取Bean类定义的所有成员变量（Bean Field）
                Field[]beanFields=beanClass.getDeclaredFields();
                if(ArrayUtil.isNotEmpty(beanFields)){
                    //遍历BeanField
                    for (Field beanField:beanFields){
                        //判断当前BeanField是否带有Inject注解
                        if(beanField.isAnnotationPresent(Inject.class)){
                            //在BeanMap中获取BeanField对应的实例
                            Class<?>beanFieldClass=beanField.getType();
                            Object beanFieldInstance=beanMap.get(beanFieldClass);//这里没有获取到？
                           // Object beanFieldInstance=ReflectionUtil.newInstance(beanFieldClass);
                            if(beanFieldInstance!=null){
                                ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.yisheng.framework.helper;

import com.yisheng.framework.annotation.Action;
import com.yisheng.framework.bean.Handler;
import com.yisheng.framework.bean.Request;
import com.yisheng.framework.util.ArrayUtil;
import com.yisheng.framework.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 * Created by easom on 2017/3/27.
 */
public final class ControllerHelper {

    //存放请求与处理器的映射关系（Action Map）
    private static final Map<Request,Handler> ACTION_MAP=new HashMap<Request,Handler>();

    static {
        //获取所有的Controller类
        Set<Class<?>> controllerClassSet=ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)){
            //遍历这些Controller类
            for (Class<?>controllerClass:controllerClassSet){
                //
                Method[]methods=controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)){
                    //遍历这些Controller类中的方法
                    for(Method method:methods){
                        //判断当前是否带有Action注解
                        if(method.isAnnotationPresent(Action.class)){
                            //从Action注解中获取URL映射规则
                            Action action=method.getAnnotation(Action.class);
                            String mapping=action.value();
                            //验证url映射规则
                            if(mapping.matches("\\w+:/\\w*")){
                                String[]array=mapping.split(":");
                                if(ArrayUtil.isNotEmpty(array)&&array.length==2){
                                    //获取请求方法和请求路径
                                    String requestMethod=array[0];
                                    String requestPath=array[1];
                                    Request request=new Request(requestMethod,requestPath);
                                    Handler handler=new Handler(controllerClass,method);
                                    //初始化ActionMap
                                    ACTION_MAP.put(request,handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /*
     *获取Handler
     */
    public static Handler getHandler(String requestMethod,String requestPath){
        Request request=new Request(requestMethod,requestPath);
        return ACTION_MAP.get(request);
    }
}

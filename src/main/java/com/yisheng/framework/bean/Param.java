package com.yisheng.framework.bean;

import com.yisheng.framework.util.CastUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 请求参数对象
 * Created by easom on 2017/3/27.
 */
public class Param {
    private Map<String,Object>paramMap;

    //获取所有字段信息
    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public Param(Map<String,Object>paramMap){
        this.paramMap=paramMap;
    }

    //根据参数名获取long型参数值
    public long getLong(String name){
        return CastUtil.castLong(paramMap.get(name));
    }

    //根据参数名获取int型参数值
    public int getInt(String name){return  CastUtil.castInt(paramMap.get(name));}

    //根据参数名获取String型参数值
    public String getString(String name){
        return  CastUtil.castString(paramMap.get(name));
    }

    //获取response
    public HttpServletResponse getResponse(){
        return (HttpServletResponse)paramMap.get("response");
    }
}

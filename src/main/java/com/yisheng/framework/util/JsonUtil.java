package com.yisheng.framework.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON工具类
 *  DispatcherServlet用到
 * Created by easom on 2017/3/28.
 */
public final class JsonUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();

    /*
    将POJO转为JSON
     */
    public static <T>String toJson(T obj){
        String json;
        try {
            json=OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("convert POJO to JSON failure",e);
            throw new RuntimeException(e);
        }
        return json;
    }
    /*
    将JSON转为POJO
     */
    public static <T> T fromJson(String json,Class<T>type){
        T pojo;
        try{
            pojo=OBJECT_MAPPER.readValue(json,type);
        }catch (Exception e){
            LOGGER.error("convert JSON to POJO failure",e);
            throw  new RuntimeException(e);
        }
        return pojo;
    }

    public static Map<String,Object> toMap(String json){

        Map<String,Object> paramMap=new HashMap<String,Object>();
        try {
            paramMap=OBJECT_MAPPER.readValue(json,Map.class);
        } catch (Exception e) {
            LOGGER.error("convert JSON to Map failure",e);
            throw  new RuntimeException(e);
        }
        return paramMap;
    }
}

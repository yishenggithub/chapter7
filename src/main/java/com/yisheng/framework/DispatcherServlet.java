package com.yisheng.framework;

import com.yisheng.framework.bean.Data;
import com.yisheng.framework.bean.Handler;
import com.yisheng.framework.bean.Param;
import com.yisheng.framework.bean.View;
import com.yisheng.framework.helper.BeanHelper;
import com.yisheng.framework.helper.ConfigHelper;
import com.yisheng.framework.helper.ControllerHelper;
import com.yisheng.framework.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by easom on 2017/3/27.
 *
 */
@WebServlet(urlPatterns = "/*",loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig)throws ServletException{
        //初始化相关Helper类
        HelperLoader.init();
        //获取ServletContext对象（用于注册servlet）
        ServletContext servletContext=servletConfig.getServletContext();
        //注册处理JSP的servlet
        ServletRegistration jspServlet=servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath()+"*");
        //注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet=servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");

    }
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException,IOException{
        //获取请求方式和请求路径
        String requestMethod=request.getMethod().toLowerCase();
        String requestPath=request.getPathInfo();

        //获取Action处理器
        Handler handler= ControllerHelper.getHandler(requestMethod,requestPath);

        if(handler!=null){

            //获取Controller类以其Bean实例
            Class<?>controllerClass=handler.getControllerClass();
            Object controllerBean= BeanHelper.getBean(controllerClass);//反射调用controller中的action方法
            //创建请求参数对象
            Map<String,Object> paramMap=new HashMap<String,Object>();


            String body= CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));//将请求体转为字符串
            //todo
            System.out.println("body:"+body);

            if(StringUtil.isNotEmpty(body)) {
                // String[]params=StringUtil.splitString(body,"&");
                if (request.getContentType() != null) { //判断是不是json
                    String[] contentType = request.getContentType().split(";");
                    if (contentType[0].equals("application/json")) {
                        paramMap = JsonUtil.toMap(body);
                        paramMap.put("jsonBody",body);
                    }
                } else {
                    String[] params = body.split("&");
                    if (ArrayUtil.isNotEmpty(params)) {
                        for (String param : params) {
                            //    String[]array=StringUtil.splitString(param,"=");
                            String[] array = param.split("=");
                            if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                String paramName = array[0];
                                String paramValue = array[1];
                                paramMap.put(paramName, paramValue);
                            }

                        }
                    }
                }
            }
                //调换一下顺序
                //将HttpServletResponse response放入到param，以便调用
                paramMap.put("response",response);

                Enumeration<String>paramNames=request.getParameterNames();
                while (paramNames.hasMoreElements()){
                    String  paramName=paramNames.nextElement();
                    String  paramValue=request.getParameter(paramName);
                    //todo
                    System.out.println("paramValue:"+paramValue);
                    paramMap.put(paramName,paramValue);
                }

            Param param=new Param(paramMap);
            //调用Action方法
            Method actionMethod=handler.getActionMethod();
            Object result= ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
            //处理Action方法返回值
            if(result instanceof View){
                //返回JSP页面
                View view=(View)result;
                String path=view.getPath();
                if(StringUtil.isNotEmpty(path)){
                    if(path.startsWith("/")){
                        response.sendRedirect(request.getContextPath()+path);
                    }else{
                        Map<String,Object>model=view.getModel();
                        for(Map.Entry<String,Object>entry:model.entrySet()){
                            request.setAttribute(entry.getKey(),entry.getValue());
                        }
                        request.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(request,response);
                        // response.sendRedirect(request.getContextPath()+ConfigHelper.getAppJspPath()+path);
                    }
                }
            }else if (result instanceof Data){
                //返回JSON数据
                Data data=(Data)result;
                Object model=data.getModel();
                if (model!=null){
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter writer=response.getWriter();
                    String json=JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }

        }
    }
}

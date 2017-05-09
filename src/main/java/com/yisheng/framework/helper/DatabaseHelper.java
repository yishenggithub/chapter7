package com.yisheng.framework.helper;

import com.yisheng.framework.util.CollectionUtil;
import com.yisheng.framework.util.PropsUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by easom on 2017/3/10.
 * 数据库操作类
 */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final ThreadLocal<Connection> CONNECTION_HOLDER=new ThreadLocal<Connection>();//写到这里

    static {
        Properties conf = PropsUtil.loadProps("smart.properties");
        DRIVER = conf.getProperty("smart.framework.jdbc.driver");
        URL = conf.getProperty("smart.framework.jdbc.url");
        USERNAME = conf.getProperty("smart.framework.jdbc.username");
        PASSWORD = conf.getProperty("smart.framework.jdbc.password");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.error("can not load jdbc driver", e);
        }
    }

    //获取数据库连接
    public static Connection getConnection(){
       /* Connection conn=null;

        try {
            conn= DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure",e);
        }
        return conn;*/
       Connection conn=CONNECTION_HOLDER.get();
       if(conn==null){
           try{
               conn=DriverManager.getConnection(URL,USERNAME,PASSWORD);
           }catch (SQLException e){
               LOGGER.error("get connection failure",e);
               throw new RuntimeException(e);
           }finally {
               CONNECTION_HOLDER.set(conn);
           }
       }
       return conn;
    }

    //关闭数据库连接
    public static  void closeConnection(){
        /*if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure",e);
            }
        }*/
        Connection conn=CONNECTION_HOLDER.get();
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure",e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    //查询实体列表
    public static <T>List<T>queryEntityList(Class<T>entityClass){

        List<T>entityList ;
        String sql="select * from "+getTableName(entityClass);
        try {
            Connection conn=getConnection();
            entityList=QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass));
        } catch (SQLException e) {
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entityList;
    }
    //查询实体列表
    public static <T>List<T>queryEntityList(Class<T>entityClass,int uid,Object... params){
       /* List<T> entityList;
        try {
            entityList=QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass));
        } catch (SQLException e) {
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
        return entityList;*/
       List<T>entityList ;
        String sql="select * from "+getTableName(entityClass)+" where uid=?";
        try {
            Connection conn=getConnection();
            entityList=QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass),new Object[]{uid});
        } catch (SQLException e) {
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entityList;
    }
    //查询实体根据uid
    public static <T> T queryEntity(Class<T>entityClass,int uid,Object...params){
        T entity;
        try {
            Connection conn=getConnection();
            String sql="SELECT * FROM "+getTableName(entityClass)+" where uid=?";
            entity=QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass),new Object[]{uid});
        } catch (Exception e) {
            LOGGER.error("query entity failure",e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entity;
    }
    //查询实体根据uid
    public static <T> T queryEntityByBid(Class<T>entityClass,int uid,Object...params){
        T entity;
        try {
            Connection conn=getConnection();
            String sql="SELECT * FROM "+getTableName(entityClass)+" where bid=?";
            entity=QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass),new Object[]{uid});
        } catch (Exception e) {
            LOGGER.error("query entity failure",e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entity;
    }
    //执行查询语句  返回多表
    public static List<Map<String,Object>> executeQuery(String sql,Object...params){
        List<Map<String,Object>>result;
        try {
            Connection conn =getConnection();
            result=QUERY_RUNNER.query(conn,sql, new MapListHandler() ,params);
        } catch (Exception e) {
            LOGGER.error("execute query failure",e);
            throw  new RuntimeException(e);
        }
        return result;
    }
    ////////////////////////
    //执行更新语句
    public static int executeUpdate(String sql,Object... params){
        int rows=0;
        try {
            Connection conn=getConnection();
            rows=QUERY_RUNNER.update(conn,sql,params);
        } catch (SQLException e) {
            LOGGER.error("excute update failure",e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return rows;
    }
    //插入实体
    public static <T>boolean insertEntity(Class<T>entityClass,Map<String,Object>fieldMap){
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not insert:fieldMap is empty");
            return false;
        }

        String sql="INSERT INTO "+getTableName(entityClass);
        StringBuilder columns=new StringBuilder("(");
        StringBuilder values=new StringBuilder("(");
        for(String fieldName:fieldMap.keySet()){
           columns.append(fieldName).append(", ");
           values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(),")");
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql+=columns+" VALUES "+ values;

        Object[] params=fieldMap.values().toArray();
        return executeUpdate(sql,params)==1;
    }
    //更新实体
    public static <T> boolean updateEntity(Class<T>entityClass,int id,Map<String,Object>fieldMap){
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not update entity:fieldMap is empty");
            return false;
        }
        String sql="UPDATE "+getTableName(entityClass)+" SET ";
        StringBuilder columns=new StringBuilder();
        for (String fieldName:fieldMap.keySet()){
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0,columns.lastIndexOf(", "))+" WHERE bid =?";

        List<Object> paramList=new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params=paramList.toArray();

        return executeUpdate(sql,params)==1;
    }
    //删除实体
    public static <T> boolean deleteEntity(Class<T> entityClass,int id){
        String sql="DELETE FROM "+getTableName(entityClass)+" WHERE bid=?";
        return executeUpdate(sql,id)==1;
    }

    //获取表的名字
    private static String getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }


}
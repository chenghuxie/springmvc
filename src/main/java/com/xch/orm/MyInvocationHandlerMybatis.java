package com.xch.orm;

import com.xch.annonation.ExtInsert;
import com.xch.annonation.ExtParam;
import com.xch.annonation.ExtSelect;
import com.xch.utils.JDBCUtils;
import com.xch.utils.SQLUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiech
 * @create 2020-04-15 16:07
 */
public class MyInvocationHandlerMybatis implements InvocationHandler {
    /**
     * 真实对象
     */
    private Object subject;

    public MyInvocationHandlerMybatis(Object subject) {
        this.subject = subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1,判断方法上面是否存在insert注解
        ExtInsert extInsert = method.getAnnotation(ExtInsert.class);
        if(extInsert!=null){
            //有注解
            //2,获取注解的SQL语句
            String insertSql=extInsert.value();
            System.out.println("注解SQL语句："+insertSql);
            //3，获取方法上的参数，绑在一起
            ConcurrentHashMap paramMap = getMethodParam(method, args);
            //获取sql语句中参数的名称
            String[] sqlInsertParameter = SQLUtils.sqlInsertParameter(insertSql);
            //定义对应参数对应值的存储容器
            List<Object> sqlParamValue=new ArrayList<>();
            for (String sqlParamName : sqlInsertParameter) {
                //获取传入的参数值
                Object paramValue=paramMap.get(sqlParamName);
                //将值放入容器中
                sqlParamValue.add(paramValue);
            }
            //6，将sql语句替换成？
            String newInsertSql = SQLUtils.parameQuestion(insertSql, sqlInsertParameter);
            System.out.println("执行的sql语句："+newInsertSql+"参数："+sqlParamValue.toString());
            //7，调用jdbc底层执行sql语句
            return JDBCUtils.insert(newInsertSql,false,sqlParamValue);
        }

        //判断是否是有ExtSelect注解
        ExtSelect extSelect = method.getDeclaredAnnotation(ExtSelect.class);
        if(extSelect!=null){
            //获取sql语句
            String selectSql = extSelect.value();
            System.out.println("注解SQL："+selectSql);
            //获取方法参数
            ConcurrentHashMap selectMap = getMethodParam(method, args);
            //获取sql语句中的参数
            List<String> paramNames = SQLUtils.sqlSelectParameter(selectSql);
            List paramValues=new ArrayList();
            for (String paramName : paramNames) {
                Object paramValue = selectMap.get(paramName);
                paramValues.add(paramValue);
            }
            //将sql语句中的参数换成？
            String newSelectSql = SQLUtils.parameQuestion(selectSql, paramNames);
            //7，调用jdbc底层执行sql语句
            ResultSet resultSet = JDBCUtils.query(newSelectSql, paramValues);
            //8,处理结果集
            if(!resultSet.next()){
                //没有查询到数据
                return null;
            }
            //指针前移一步
            resultSet.previous();
            return setResultSet(method,resultSet);
        }
        return null;
    }

    /**
     * 获取参数名称和值绑定在一起
     * @param method
     * @param args
     * @return
     */
    private ConcurrentHashMap getMethodParam(Method method, Object[] args){
        ConcurrentHashMap paramMap=new ConcurrentHashMap();
        Parameter[] parameters = method.getParameters();
        for (int i=0;i<parameters.length;i++) {
            Parameter parameter = parameters[i];
            //判断参数上面是否含有注解
            ExtParam extParam = parameter.getDeclaredAnnotation(ExtParam.class);
            if(extParam!=null){
                String paramName = extParam.value();
                //方法参数值
                Object value = args[i];
                //参数与值绑在一起
                paramMap.put(paramName,value);
            }
        }
        return paramMap;
    }

    public Object setResultSet(Method method,ResultSet resultSet) throws SQLException, IllegalAccessException, InstantiationException {
        //获取返回值类型
        Class<?> methodReturnType = method.getReturnType();
        //实例化对象
        Object instance = methodReturnType.newInstance();
        while (resultSet.next()){
            //获取当前对象的所有属性
            Field[] fields = methodReturnType.getDeclaredFields();
            for (Field field : fields) {
                //获取属性名称
                String fieldName = field.getName();
                //获取属性值
                Object fieldValue = resultSet.getObject(fieldName);
                //设置私有方法可以访问
                field.setAccessible(true);
                //设置属性值
                field.set(instance,fieldValue);
            }
        }
        return instance;
    }
}

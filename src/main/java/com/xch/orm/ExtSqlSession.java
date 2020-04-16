package com.xch.orm;

import java.lang.reflect.Proxy;

/**
 * @author xiech
 * @create 2020-04-16 10:03
 */
public class ExtSqlSession {
    public static <T> T getMapper(Class<T> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException{
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new MyInvocationHandlerMybatis(clazz));
    }
}

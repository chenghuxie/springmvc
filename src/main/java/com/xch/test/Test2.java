package com.xch.test;

import com.xch.bean.User;
import com.xch.mapping.UserMapper;
import com.xch.orm.ExtSqlSession;

/**
 * @author xiech
 * @create 2020-04-15 11:12
 */
public class Test2 {
    /**
     * 1,接口不能实例化，那么如何调用的？
     * 字节码技术，匿名内部类，动态代理方式
     *
     */
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        UserMapper userMapper=ExtSqlSession.getMapper(UserMapper.class);
        userMapper.insertUser("xiech",21);
        User user = userMapper.selectUser("test01", 25);
        System.out.println("name:"+user.getName()+",age:"+user.getAge());

    }

}

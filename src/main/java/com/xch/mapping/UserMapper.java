package com.xch.mapping;

import com.xch.annonation.ExtInsert;
import com.xch.annonation.ExtParam;
import com.xch.annonation.ExtSelect;
import com.xch.bean.User;

/**
 * @author xiech
 * @create 2020-04-15 16:45
 */
public interface UserMapper {

    @ExtInsert("insert into user(name,age) values(#{name},#{age})")
    public int insertUser(@ExtParam("name") String name, @ExtParam("age") Integer age);

    @ExtSelect("select * from user where name=#{name} and age=#{age} ")
    User selectUser(@ExtParam("name") String name, @ExtParam("age") Integer age);
}

package com.xch.annonation;

import java.lang.annotation.*;

/**
 * @author xiech
 * @create 2020-04-15 11:18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExtInsert {
    String value();
}

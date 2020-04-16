package com.xch.annonation;

import java.lang.annotation.*;

/**
 * @author xiech
 * @create 2020-04-16 10:25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExtSelect {
    String value();
}

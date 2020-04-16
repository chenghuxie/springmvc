package com.xch.annonation;

import java.lang.annotation.*;

/**
 * @author xiech
 * @create 2020-04-15 16:43
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ExtParam {
    String value();
}

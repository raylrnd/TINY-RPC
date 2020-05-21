package com.example.tinyrpc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 2:55 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {
    Class<?> interfaceClass() default void.class;
    boolean callback() default false;
    String callbackMethod() default "";
    int callbackParamIndex() default 1;
}

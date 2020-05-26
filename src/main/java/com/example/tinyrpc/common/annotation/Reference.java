package com.example.tinyrpc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 2:55 下午
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {
    boolean async() default false;
    boolean callback() default  false;
    boolean oneway() default  false;
    long timeout() default 3000;
    String serializer() default "protobuff";
    String proxy() default "jsk";
//    String callbackMethod() default "";
//    int callbackParamIndex() default 1;
}

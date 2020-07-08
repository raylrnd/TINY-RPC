package com.example.autoconfig.annotation;

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
    boolean oneway() default  false;
    long timeout() default 50000;
    String serializer() default "";
    String proxy() default "";
    String protocol() default "";
    String[] filter() default {};
    String registry() default "";
    /**
     * Load balance strategy, legal values include: random, roundrobin, leastactive
     */
    String loadbalance() default "";
    /**
     * When enable, prefer to call local service in the same JVM if it's present, default value is true
     */
    boolean injvm() default true;
}

package com.example.tinyrpc.common;

/**
 * @auther zhongshunchao
 * @date 2020/5/19 6:30 下午
 */
// 封装对方发送过来的request请求
public class Invocation {

    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;


}

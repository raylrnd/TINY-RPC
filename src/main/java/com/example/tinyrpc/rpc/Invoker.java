package com.example.tinyrpc.rpc;

/**
 * @auther zhongshunchao
 * @date 05/05/2020 15:02
 */

public class Invoker {
    //方法名
    private String method;
    //参数列表
    private Object[] args;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}

package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:16 上午
 */
public class DubboInvoker<T> extends AbstractInvoker<T> {
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

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return null;
    }
}

package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;

/**
 * @auther zhongshunchao
 * @date 2020/5/20 8:27 下午
 */
public interface Invoker<T> {

    Class<T> getInterface();

    Object invoke(Invocation invocation) throws Exception;
}

package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Request;

/**
 * @auther zhongshunchao
 * @date 13/06/2020 20:52
 */
public interface Invoker {

    String getInterface();

    Object invoke(Request request);
}

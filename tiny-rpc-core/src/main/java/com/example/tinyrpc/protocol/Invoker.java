package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;

/**
 * @auther zhongshunchao
 * @date 13/06/2020 20:52
 */
public interface Invoker {

    URL getUrl();

    Class<?> getInterface();

    Object invoke(Invocation invocation);

    void destroy();
}

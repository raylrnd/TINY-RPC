package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.URL;

/**
 * @auther zhongshunchao
 * @date 13/06/2020 20:52
 */
public interface Invoker {

    URL getUrl();

    Class<?> getInterface();

    Object invoke(Invocation invocation) throws Exception;

    void destroy();
}

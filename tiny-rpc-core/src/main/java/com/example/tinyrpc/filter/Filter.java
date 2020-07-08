package com.example.tinyrpc.filter;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.protocol.Invoker;

/**
 * @auther zhongshunchao
 * @date 27/06/2020 20:55
 */
public interface Filter {
    Object invoke(Invoker invoker, Invocation invocation) throws Exception;
}

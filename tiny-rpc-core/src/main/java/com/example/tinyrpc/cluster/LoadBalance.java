package com.example.tinyrpc.cluster;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;

import java.util.List;

/**
 * @auther zhongshunchao
 * @date 20/06/2020 21:01
 */
public interface LoadBalance {

    Invoker select(List<Invoker> invokers);

}

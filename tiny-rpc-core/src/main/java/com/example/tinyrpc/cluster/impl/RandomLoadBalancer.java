package com.example.tinyrpc.cluster.impl;

import com.example.tinyrpc.cluster.LoadBalance;
import com.example.tinyrpc.protocol.Invoker;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @auther zhongshunchao
 * @date 21/06/2020 12:39
 */
public class RandomLoadBalancer implements LoadBalance {

    @Override
    public Invoker select(List<Invoker> invokers) {
        if (invokers.size() == 0) {
            return null;
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
    }
}

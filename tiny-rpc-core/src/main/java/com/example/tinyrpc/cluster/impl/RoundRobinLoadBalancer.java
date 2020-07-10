package com.example.tinyrpc.cluster.impl;

import com.example.tinyrpc.cluster.LoadBalance;
import com.example.tinyrpc.protocol.Invoker;
import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalance {

    private int index = 0;

    @Override
    public Invoker select(List<Invoker> invokers) {
        if(invokers.size() == 0) {
            return null;
        }
        Invoker result = invokers.get(index);
        index = (index + 1) % invokers.size();
        return result;
    }
}

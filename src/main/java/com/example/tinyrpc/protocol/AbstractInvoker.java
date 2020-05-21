package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.transport.Client;

/**
 * @auther zhongshunchao
 * @date 2020/5/20 8:29 下午
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {

    //@Autowired
    private Client client;

    @Override
    public Class<T> getInterface() {
        return null;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

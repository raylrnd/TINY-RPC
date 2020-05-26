package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.transport.Client;

/**
 * @auther zhongshunchao
 * @date 2020/5/20 8:29 下午
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {

    public Class interfaceClass;
    //@Autowired
    private Client client;

    public AbstractInvoker(Class interfaceClass, Client client) {
        this.interfaceClass = interfaceClass;
        this.client = client;
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

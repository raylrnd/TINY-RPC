package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.transport.Client;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @auther zhongshunchao
 * @date 26/06/2020 21:43
 */
public class RealInvoker implements Invoker{

    private Client client;

    private int weight;

    private Class<?> interfaceClass;

    public RealInvoker(Class<?> interfaceClass, int weight) {
        this.interfaceClass = interfaceClass;
        this.weight = weight;
    }

    @Override
    public Object invoke(Invocation invocation) {
        //build Request
        Request request = new Request(123456789);
        request.setData(invocation);
        request.setIs2way(!invocation.isOneWay());
        request.setSerializationId(invocation.getSerializer());
        //应该在这里进行负载均衡
        Future<Object> future = client.send(request);
        Object response = null;
        try {
            if (future != null) {
                response = future.get(900, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response;
    }


    @Override
    public Class<?> getInterface() {
        return interfaceClass;
    }

    public int getWeight() {
        return weight;
    }


    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void destroy() {
        if (this.client != null) {
            client.close();
            client = null;
        }
    }
}

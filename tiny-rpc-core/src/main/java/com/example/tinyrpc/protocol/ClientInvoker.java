package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.transport.Client;
import java.util.concurrent.*;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:16 上午
 */
public class ClientInvoker implements Invoker {

    private Client client;

    private Class<?> interfaceClass;

    public ClientInvoker(Client client, Class<?> interfaceClass) {
        this.client = client;
        this.interfaceClass = interfaceClass;
    }


    @Override
    public Class<?> getInterface() {
        return interfaceClass;
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
    public void destroy() {
        if (this.client != null) {
            client.close();
            client = null;
        }
    }
}

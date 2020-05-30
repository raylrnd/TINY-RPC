package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.utils.FutureContext;
import com.example.tinyrpc.transport.Client;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.concurrent.*;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:16 上午
 */
public class MyInvoker {

    @Autowired
    private Client client;

    public Response invoke(Request request) {
        long requestId = request.getRequestId();
        CompletableFuture<Response> future = FutureContext.FUTURE_CACHE.putIfAbsent(requestId, new CompletableFuture());
        client.send(request);
        Response response = null;
        try {
            response = future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response;
    }
}

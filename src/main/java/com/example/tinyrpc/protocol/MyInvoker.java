package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.utils.FutureContext;
import com.example.tinyrpc.transport.Client;
import com.example.tinyrpc.transport.client.NettyClient;
import java.util.concurrent.*;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:16 上午
 */
public class MyInvoker {

    private Client client = new NettyClient();

    public Response invoke(Request request) {
        long requestId = request.getRequestId();
        CompletableFuture<Response> future = new CompletableFuture();
        FutureContext.FUTURE_CACHE.putIfAbsent(requestId, future);
        client.send(request);
        Response response = null;
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
}

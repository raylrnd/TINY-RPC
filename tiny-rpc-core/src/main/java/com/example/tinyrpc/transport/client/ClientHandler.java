package com.example.tinyrpc.transport.client;

import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.utils.FutureContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.*;

import static com.example.tinyrpc.common.Response.SERVICE_ERROR;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:33
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    //读取responde消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        if (response == null || response.getResponseBody() == null) {
            // log
        }
        //解析状态码，如果是500，则不要向上传递result了，直接抛出异常
        if (response.getStatus() == SERVICE_ERROR) {
            //log
            System.out.println(response.getResponseBody().getErrorMsg());
        }
        long requestId = response.getRequestId();
        CompletableFuture future = FutureContext.FUTURE_CACHE.remove(requestId);
        if (future == null) {
            throw new Exception("requestId错误，response没有对应的request相匹配");
        }
        future.complete(response.getResponseBody().getResult());
    }
}

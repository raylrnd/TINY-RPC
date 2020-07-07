package com.example.tinyrpc.transport.client;

import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.common.utils.FutureContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static com.example.tinyrpc.common.Response.SERVICE_ERROR;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:33
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    //读取responde消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        logger.info("ClientHandler received Response:" + response);
        if (response == null || response.getResponseBody() == null) {
            throw new BusinessException("Response is null");
        }
        //解析状态码，如果是500，则不要向上传递result了，直接抛出异常
        if (response.getStatus() == SERVICE_ERROR) {
            throw new BusinessException(response.getResponseBody().getErrorMsg());
        }
        long requestId = response.getRequestId();
        CompletableFuture future = FutureContext.FUTURE_CACHE.remove(requestId);
        if (future == null) {
            throw new BusinessException("RequestId is invalid，no matched requestId");
        }
        future.complete(response.getResponseBody().getResult());
    }
}

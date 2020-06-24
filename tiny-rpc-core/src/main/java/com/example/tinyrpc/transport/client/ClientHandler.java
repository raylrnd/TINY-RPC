package com.example.tinyrpc.transport.client;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.common.utils.FutureContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

import static com.example.tinyrpc.common.Response.SERVICE_ERROR;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:33
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    //读取responde消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        log.info("客户端 ClientHandler 收到Response为：" + JSON.toJSONString(response));
        if (response == null || response.getResponseBody() == null) {
            throw new BusinessException("response is null");
        }
        //解析状态码，如果是500，则不要向上传递result了，直接抛出异常
        if (response.getStatus() == SERVICE_ERROR) {
            throw new BusinessException(response.getResponseBody().getErrorMsg());
        }
        long requestId = response.getRequestId();
        CompletableFuture future = FutureContext.FUTURE_CACHE.remove(requestId);
        if (future == null) {
            throw new Exception("requestId错误，response没有对应的request相匹配");
        }
        future.complete(response.getResponseBody().getResult());
    }
}

package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.ResponseBody;
import com.example.tinyrpc.config.ServiceConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;


/**
 * @auther zhongshunchao
 * @date 05/05/2020 15:02
 */
// 消息被读取后，会自动释放资源
public class ServerHandler extends SimpleChannelInboundHandler<Request> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        //调用代理，通过反射的方式调用本地jvm中的方法
        Response response = new Response(request.getRequestId());
        Invocation data = request.getData();
        String className = data.getClassName();
        Object bean = ServiceConfig.SERVICE_MAP.get(className);
        Method method = bean.getClass().getMethod(data.getMethodName(), data.getParameterTypes());
        Object result = method.invoke(bean, data.getParameters());
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResult(result);
        response.setResponseBody(responseBody);
        ctx.writeAndFlush(response);
    }
}

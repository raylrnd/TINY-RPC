package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;
import com.example.tinyrpc.common.*;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.config.ServiceConfig;
import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.client.ClientHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

import static com.example.tinyrpc.codec.Codec.*;

/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */

/**
 * RPC的执行流程
 * 1）在项目启动的时候，进行服务的注册和发现。设计到的层次为Protocol、Registry
 *
 * 2）进入RPC调用的过程
 *  收到Client发来的请求or响应消息，根据序列化框架反序列化出消息体中的内容，用Invoker表示该内容。
 *  利用Proxy实现透明化的服务调用，将调用的结果回传给Client端
 */
public class NettyServer implements Server {

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    private ServerBootstrap bootstrap;

    private String address;

    //当前任务为CPU密集型，corePoolSize = CPU * 2, maximumPoolSize = CPU * 4, 空闲存活时间1s
    private ExecutorService executor = ExtensionLoader.getDefaultExecutor();

    public NettyServer(String address) {
        this.address = address;
        this.bootstrap = new ServerBootstrap();
    }
    //Start Server
    public void open() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
//                            插入到ChannelHandlerContext这个双链表当中
                            ch.pipeline()
                                    .addLast(new Encoder())
                                    .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP))
                                    .addLast(new Decoder())
                                    .addLast(new ServerHandler(NettyServer.this));
                        }
                    });
            String[] ipAndPort = address.trim().split(":");
            ChannelFuture future = bootstrap.bind(ipAndPort[0], Integer.valueOf(ipAndPort[1])).sync();
            this.channel = future.channel();
            log.info("Server successfully bind at : {}" + address);
        } catch (InterruptedException e) {
            throw new BusinessException("Server can not bind address:" + address);
        }
    }

    public void handleRequest(ChannelHandlerContext ctx, Request request) throws Exception{

        executor.submit(() -> {
            //调用代理，通过反射的方式调用本地jvm中的方法
            Response response = new Response(request.getRequestId());
            Invocation data = request.getData();
            String className = data.getServiceName();
            Object bean = ServiceConfig.SERVICE_MAP.get(className);

            Method method = null;
            try {
                method = bean.getClass().getMethod(data.getMethodName(), data.getParameterTypes());
                Object result = method.invoke(bean, data.getArguments());
                ResponseBody responseBody = new ResponseBody();
                responseBody.setResult(result);
                response.setResponseBody(responseBody);
                ctx.writeAndFlush(response);
            } catch (Exception e) {
                log.error("HandleRequest error, exception:" + e.getMessage());
            }


        });
    }

    @Override
    public void start() {
        open();
    }

    @Override
    public void stop() {
        this.channel.closeFuture();
    }
}

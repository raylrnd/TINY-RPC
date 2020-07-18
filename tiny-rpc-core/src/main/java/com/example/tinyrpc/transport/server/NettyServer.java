package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;
import com.example.tinyrpc.common.domain.*;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.config.ServiceConfig;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.transport.AbstractEndpoint;
import com.example.tinyrpc.transport.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static com.example.tinyrpc.codec.Codec.*;
/**
 * RPC的执行流程
 * 1）在项目启动的时候，进行服务的注册和发现。设计到的层次为Protocol、Registry
 *
 * 2）进入RPC调用的过程
 *  收到Client发来的请求or响应消息，根据序列化框架反序列化出消息体中的内容，用Invoker表示该内容。
 *  利用Proxy实现透明化的服务调用，将调用的结果回传给Client端
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */
public class NettyServer extends AbstractEndpoint implements Server {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ServerBootstrap bootstrap;

    public NettyServer(String address) {
        super(address);
        this.bootstrap = new ServerBootstrap();
    }

    @Override
    public void start() {
        executor.submit(() -> {
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
                                        .addLast("IdleStateHandler", new IdleStateHandler(Constants.HEART_BEAT_TIME_OUT_MAX_TIME * Constants.HEART_BEAT_TIME_OUT, 0, 0))
                                        .addLast(new Encoder())
                                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP))
                                        .addLast(new Decoder())
                                        .addLast(new ServerHandler(NettyServer.this));
                            }
                        });
                String[] ipAndPort = address.trim().split(":");
                ChannelFuture future = bootstrap.bind(ipAndPort[0], Integer.valueOf(ipAndPort[1])).sync();
                channel = future.channel();
                logger.info("Server successfully bind at : {}" + address);
            } catch (InterruptedException e) {
                logger.error("Server can not bind address:" + address);
                e.printStackTrace();
                throw new BusinessException("Server can not bind address:" + address);
            }
        });
    }

    @Override
    public Future<Object> send(Request request) {
        channel.writeAndFlush(request);
        return null;
    }

    @Override
    public void received(ChannelHandlerContext ctx, Object msg) {
        executor.submit(() -> {
            if (msg instanceof Request) {
                Request request = (Request) msg;
                //调用代理，通过反射的方式调用本地jvm中的方法
                long requestId = request.getRequestId();
                Response response = new Response(requestId);
                Invocation invocation = request.getData();
                invocation.setSide(Constants.SERVER_SIDE);
                URL url = new URL();
                url.setOneWay(request.isOneway());
                invocation.setUrl(url);
                String className = invocation.getServiceName();
                Invoker invoker = ServiceConfig.INVOKER_MAP.get(className);
                try {
                    Object result = invoker.invoke(invocation);
                    // oneway调用则直接返回
                    if (invocation.getUrl().isOneWay()) {
                        return;
                    }
                    ResponseBody responseBody = new ResponseBody();
                    responseBody.setResult(result);
                    response.setResponseBody(responseBody);
                    ctx.writeAndFlush(response);
                } catch (Exception e) {
                    logger.error("HandleRequest error, exception:{}", e.getMessage());
                    e.printStackTrace();
                    throw new BusinessException("HandleRequest error, exception:" + e.getMessage());
                }
            }
        });
    }

}

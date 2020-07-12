package com.example.tinyrpc.transport.client;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;
import com.example.tinyrpc.common.domain.Request;
import com.example.tinyrpc.common.domain.Response;
import com.example.tinyrpc.common.domain.RpcContext;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.common.utils.FutureContext;
import com.example.tinyrpc.transport.AbstractEndpoint;
import com.example.tinyrpc.transport.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.example.tinyrpc.codec.Codec.*;
import static com.example.tinyrpc.common.domain.Response.SERVICE_ERROR;

/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */
public class NettyClient extends AbstractEndpoint implements Client {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final NioEventLoopGroup NIO_EVENT_LOOP_GROUP = new NioEventLoopGroup();

    private Bootstrap bootstrap;


    public NettyClient(String address) {
        super(address);
        this.bootstrap = new Bootstrap();
        connect();
    }

    public void connect() {
        executor.submit(() -> {
            String[] ipAndPort = address.trim().split(":");
            ChannelFuture future = bootstrap.connect(ipAndPort[0], Integer.valueOf(ipAndPort[1]));
            channel = future.channel();
            logger.info("Client CONNECTED at: {}", address);
        });
    }

    @Override
    public void start() {
        bootstrap.group(NIO_EVENT_LOOP_GROUP)
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        //Encoder: netty自带编码器，可以自动将长度加到头部
                        ch.pipeline()
                                //Encoder: message to byte
                                .addLast(new Encoder())
                                .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP))
                                .addLast(new Decoder())
                                //自定义处理逻辑，解析请求
                                .addLast(new ClientHandler(NettyClient.this));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        logger.info("Client OPEN!");
    }

    @Override
    public Future<Object> send(Request request) {
        CompletableFuture<Object> responseFuture = new CompletableFuture<>();
        executor.submit(() -> {
            FutureContext.FUTURE_CACHE.putIfAbsent(request.getRequestId(), responseFuture);
            channel.writeAndFlush(request);
        });
        return responseFuture;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void received(ChannelHandlerContext ctx, Object msg) {
        executor.submit(() -> {
            // 延迟删除Attachments
            RpcContext.getContext().clearAttachments();
            if (msg == null) {
                logger.error("msg is null");
                throw new BusinessException("msg is null");
            }
            if (msg instanceof  Response) {
                Response response = (Response) msg;
                logger.info("客户端 ClientHandler 收到Response为:{}" + JSON.toJSONString(response));
                //解析状态码，如果是500，则不要向上传递result了，直接抛出异常
                if (response.getStatus() == SERVICE_ERROR) {
                    logger.error(response.getResponseBody().getErrorMsg());
                    throw new BusinessException(response.getResponseBody().getErrorMsg());
                }
                long requestId = response.getRequestId();
                CompletableFuture future = FutureContext.FUTURE_CACHE.remove(requestId);
                if (future == null) {
                    throw new BusinessException("requestId错误，response没有对应的request相匹配");
                }
                future.complete(response.getResponseBody().getResult());
            }
        });
    }
}

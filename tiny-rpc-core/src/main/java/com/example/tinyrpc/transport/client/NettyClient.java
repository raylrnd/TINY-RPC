package com.example.tinyrpc.transport.client;

import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;
import com.example.tinyrpc.common.ExtensionLoader;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.common.utils.FutureContext;
import com.example.tinyrpc.transport.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.example.tinyrpc.codec.Codec.*;
import static com.example.tinyrpc.common.Response.SERVICE_ERROR;

/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */
public class NettyClient implements Client {

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    private static final NioEventLoopGroup NIO_EVENT_LOOP_GROUP = new NioEventLoopGroup();

    private String address;

    private Bootstrap bootstrap;

    private ExecutorService executorService = ExtensionLoader.getExtensionLoader().getDefaultExecutor();

    public NettyClient(String address) {
        this.address = address;
        this.bootstrap = new Bootstrap();
        open();
        connect();
    }

    public void open() {
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

    public void connect() {
        executorService.submit(() -> {
            String[] ipAndPort = address.trim().split(":");
            ChannelFuture future = bootstrap.connect(ipAndPort[0], Integer.valueOf(ipAndPort[1]));
            channel = future.channel();
            logger.info("Client CONNECTED at: {}", address);
        });
    }

    @Override
    public Future<Object> send(Request request) {
        CompletableFuture<Object> responseFuture = new CompletableFuture<>();
        executorService.submit(() -> {
            FutureContext.FUTURE_CACHE.putIfAbsent(request.getRequestId(), responseFuture);
            channel.writeAndFlush(request);
        });
        return responseFuture;
    }

    @Override
    public void received(ChannelHandlerContext ctx, Response response) throws Exception {
        executorService.submit(() -> {
            logger.info("客户端 ClientHandler 收到Response为:{}" + response);
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
                throw new BusinessException("requestId错误，response没有对应的request相匹配");
            }
            future.complete(response.getResponseBody().getResult());
        });
    }

    @Override
    public void close() {
        executorService.submit(() -> {
            logger.info("正在关闭 Client:{}", address);
            if (this.channel != null && channel.isOpen()) {
                try {
                    this.channel.close().sync();
                } catch (InterruptedException e) {
                    logger.error("Fail to close client, address:" + address);
                }
            }
        });
    }
}

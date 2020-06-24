package com.example.tinyrpc.transport.client;

import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.utils.FutureContext;
import com.example.tinyrpc.transport.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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

/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */
public class NettyClient implements Client {

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    private static final NioEventLoopGroup NIO_EVENT_LOOP_GROUP = new NioEventLoopGroup();

    private String address;

    private Bootstrap bootstrap;

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
                                .addLast(new ClientHandler());
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        log.info("Client OPEN!");
    }

    public void connect() {
        String[] ipAndPort = address.trim().split(":");
        ChannelFuture future = bootstrap.connect(ipAndPort[0], Integer.valueOf(ipAndPort[1]));
        channel = future.channel();
        log.info("Client CONNECTED at: " + address);
    }

    @Override
    public Future<Object> send(Request request) {
        CompletableFuture<Object> responseFuture = new CompletableFuture<>();
        FutureContext.FUTURE_CACHE.putIfAbsent(request.getRequestId(), responseFuture);
        channel.writeAndFlush(request);
        return responseFuture;
    }

    @Override
    public void close() {
        if (this.channel != null && channel.isOpen()) {
            try {
                this.channel.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("Client :" + address + "关闭异常");
            }
        }
    }
}

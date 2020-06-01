package com.example.tinyrpc.transport.client;

import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.common.utils.FutureContext;
import com.example.tinyrpc.transport.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import static com.example.tinyrpc.codec.Codec.*;



/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */
public class NettyClient implements Client {

    private Channel channel;
    public NettyClient() {
        run("127.0.0.1", 8989);
    }
    @Override
    public void run(String hostName, int port) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
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
            ChannelFuture future = bootstrap.connect(hostName, port).sync();
            channel = future.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Future<Response> send(Request request) {
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        FutureContext.FUTURE_CACHE.put(request.getRequestId(), responseFuture);
        channel.writeAndFlush(request);
        return responseFuture;
    }
}

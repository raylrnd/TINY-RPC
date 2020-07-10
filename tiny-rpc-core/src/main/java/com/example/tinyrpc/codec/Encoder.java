package com.example.tinyrpc.codec;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.domain.Request;
import com.example.tinyrpc.common.domain.Response;
import com.example.tinyrpc.serialization.impl.FastJsonSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:23
 */
public class Encoder extends MessageToByteEncoder implements Codec{

    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {
        logger.info("###Encoder will send msg :" + JSON.toJSONString(msg));
        int savedStart = buffer.writerIndex();
        buffer.writerIndex(savedStart + 4);
        buffer.writeBytes(MAGIC_ARRAY);
        if (msg instanceof Request) {
            encodeRequest((Request) msg, buffer);
        } else {
            encodeResponse((Response) msg, buffer);
        }
        int end = buffer.writerIndex();
        buffer.writerIndex(savedStart);
        buffer.writeInt(end - savedStart -4);
        buffer.writerIndex(end);
    }

    private void encodeRequest(Request request, ByteBuf buffer) {
        // header.
        byte [] flag = new byte[2];
        if (request.isOneway()) {
            flag[0] |= FLAG_TWOWAY;
        }
        flag[0] |= FLAG_REQUEST;
        // is PING
        if (request.isEvent()) {
            flag[0] |= FLAG_EVENT;
        }
        // set serialization id.
        flag[0] |= request.getSerializationId();
        buffer.writeBytes(flag);
        long requestId = request.getRequestId();
        buffer.writeLong(requestId);
        byte[] body = new FastJsonSerialization().serialize(request.getData());
        buffer.writeBytes(body);
    }

    //比request多了个status
    private void encodeResponse(Response response, ByteBuf buffer) {
        // header.
        byte [] flag = new byte[2];
        // is PONG
        if (response.isEvent()) {
            flag[0] |= FLAG_EVENT;
        }
        // set serialization id.
        flag[0] |= response.getSerializationId();
        flag[1] |= response.getStatus();
        buffer.writeBytes(flag);
        long requestId = response.getRequestId();
        buffer.writeLong(requestId);
        byte[] body = new FastJsonSerialization().serialize(response.getResponseBody());
        buffer.writeBytes(body);
    }
}

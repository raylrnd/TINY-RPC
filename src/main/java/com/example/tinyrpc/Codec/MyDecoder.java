package com.example.tinyrpc.Codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import sun.jvm.hotspot.runtime.Bytes;

import java.util.List;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:23
 */

/**
 * 自定义报文格式
 *
 */
public class MyDecoder extends ByteToMessageDecoder {
    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_EVENT = (byte) 0x20;
    // 低5位
    protected static final int SERIALIZATION_MASK = 0x1f;

    // 先读头部，解析出消息的长度
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        if (buffer.readableBytes() < 10) {
            // 头部异常
        }
        // 解析头部
        byte [] header = new byte[10];
        buffer.readBytes(header);
        byte flag = header[0];
        // 采用的序列化协议
        byte serializationProto = (byte) (flag & SERIALIZATION_MASK);
        // get request id.
        long requestId = BytesUtil.bytes2long(header, 4);
        // 开始body解析
        // 为响应报文
        if ((flag & FLAG_REQUEST) == 0) {
            // decode response.
            Response res = new Response(requestId);
            if ((flag & FLAG_EVENT) != 0) {
                res.setEvent(true);
            }
            // get status.
            byte status = header[1];
            res.setStatus(status);
            if (status == Response.OK) {
                Object data;
                // 为心跳事件，如果是PING，直接返回PONG
                if (res.isEvent()) {
                    //
                }
            }
        } else {
            // 解析请求报文

        }

    }
}

package com.example.tinyrpc.Codec;

import com.example.tinyrpc.Serialization.SerializationUtil;
import com.example.tinyrpc.Serialization.Serializer;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.rpc.Invoker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:23
 */

/**
 * 仿照Dubbo协议
 *  ------------ (32 bits)
 * | magic (16 bits)                      // 魔数，为"0xdeff"
 * | req/res (1bit)                       // 消息类型，有req/res
 * | 2way (1 bit)                         // 仅在 Req/Res 为1（请求）时才有用，标记是否期望从服务器返回值。如果需要来自服务器的返回值，则设置为1。
 * | event (1 bit)                        // 表示为PING/PONG事件，如果是req，则为PING请求；如果是res，则为PONG请求
 * | serialization_id (5 bits)            // 序列化器的ID
 * | status (8 bits)                      // 消息的状态：200/404/500等
 *  ----------- (32 bits)
 * | RPC Request ID (32 bits)
 *  ----------- (32 bits)
 * | Data Length (32 bits)
 *  -----------
 * | Data                                 // 支持变长格式
 */
// 如果是Client，接收到的入站消息应该是response，直接将response移交给上层
// 如果是Server，接收到的入站消息应该是request，利用反射得到RPC调用结果，然后发送给Client端
// 如果req == 1, 应该移交给Client端进行处理。考虑到一台机器可能同时有client和server
public class Decoder extends ByteToMessageDecoder {

    public Serializer serializer;

    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;

    protected static final int SERIALIZATION_MASK = 0x1f;
    // status
    protected static final int STATUS = 0xf0;

    // 先读头部，解析出消息的长度
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (buffer.readableBytes() < 10) {
            // 头部异常
            throw new Exception("头部异常");
        }
        // 解析头部
        byte [] header = new byte[10];
        buffer.readBytes(header);
        // 解析data length
        int dataLength = header[4] << 8 + header[5];
        if (buffer.readableBytes() != dataLength) {
            throw new Exception("实际长度与dataLength不符");
        }
        byte [] data = new byte [dataLength];
        // 取出前3位，解析
        boolean isRequest = (header[0] & FLAG_REQUEST) != 0;
        boolean is2way = (header[0] & FLAG_TWOWAY) != 0;
        boolean isEvent = (header[0] & FLAG_TWOWAY) != 0;
        // 取后5位，解析出serializationId
        byte serializationId = (byte) (header[0] & SERIALIZATION_MASK);
        // 解析status
        byte status = header[1];
        // 解析Request ID
        int id = header[2] << 8 + header[3];
        //解析消息体
        buffer.readBytes(data);
        Invoker invoker = SerializationUtil.deserializer(data, Invoker.class, serializationId);
        // 开始body解析
        // 为响应报文
        if (isRequest) {
            // 解析请求报文
            Request req = new Request(id);
            req.setEvent(isEvent);
            out.add(req);
        } else {
            // decode response.
            Response res = new Response(id);
            res.setResponse(true);
            res.setEvent(isEvent);
            res.setStatus(status);
            out.add(res);
        }


    }
}

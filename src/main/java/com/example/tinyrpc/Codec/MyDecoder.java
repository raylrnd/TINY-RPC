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
 * 仿照Dubbo协议
 *  ------------ (32 bits)
 * | magic (16 bits)                      // 魔数，为"0xdeff"
 * | req/res (1bit)                       // 消息类型，有req/res、ping/pong
 * | 2way (1 bit)                         // 仅在 Req/Res 为1（请求）时才有用，标记是否期望从服务器返回值。如果需要来自服务器的返回值，则设置为1。
 * | event (1 bit)                        // 表示为PING/PONG事件，如果是req，则为PING请求；如果是res，则为PONG请求
 * | serialization_id (5 bits)            // 序列化器的ID
 * | status (4 bits)                      // 消息的状态：200/404/500等
 * | extension_field (4 bits)             // 扩展字段
 *  ----------- (32 bits)
 * | RPC Request ID (32 bits)
 *  ----------- (32 bits)
 * | Data Length (32 bits)
 *  -----------
 * | Data                                 //支持变长格式
 */
public class MyDecoder extends ByteToMessageDecoder {
    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;

    protected static final int SERIALIZATION_MASK = 0x1f;
    //status
    protected static final int STATUS = 0xf0;

    // 先读头部，解析出消息的长度
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        if (buffer.readableBytes() < 10) {
            // 头部异常
        }
        // 解析头部
        byte [] header = new byte[10];
        buffer.readBytes(header);
        //flag包含：message_type和2way
        byte flag = header[0];
        //status_flag包含：status和扩展字段
        byte status_flag = header[1];
        // 采用的序列化协议id
        byte serializationId = (byte) (flag & SERIALIZATION_MASK);
        // get request id.
        long id = BytesUtil.bytes2long(header, 4);
        // 开始body解析
        // 为响应报文
        if ((flag & FLAG_REQUEST) == 0) {
            // decode response.
            Response res = new Response(id);
            res.setResponse(true);
            if ((flag & FLAG_EVENT) != 0) {
                res.setEvent(true);
            }
            // get status.
            byte status = header[1];
            res.setStatus(status);
        } else {
            // 解析请求报文
            Request req = new Request(id);
        }

    }
}

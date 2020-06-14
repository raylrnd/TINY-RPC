package com.example.tinyrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @auther zhongshunchao
 * @date 29/05/2020 22:06
 */
public interface Codec {
    // message flag.
    byte FLAG_REQUEST = (byte) 0x80;
    byte FLAG_TWOWAY = (byte) 0x40;
    byte FLAG_EVENT = (byte) 0x20;

    int SERIALIZATION_MASK = 0x0f;
    // status
    int STATUS = 0xf0;

    //自定义魔数"0xdeff"
    byte [] MAGIC_ARRAY = {(byte)0xde, (byte)0xff};
    ByteBuf MAGIC = Unpooled.copiedBuffer(MAGIC_ARRAY);    // 头部长度固定8个字节
//    byte [] MAGIC_ARRAY = "$$".getBytes();
//    public static final ByteBuf MAGIC = Unpooled.copiedBuffer(MAGIC_ARRAY);
    int HEAD_LENGTH = 8;
    int HEADER_LENGTH = 16;
    // 最大报文长度 8M
    int MAX_LENGTH = 1024 * 10;
    int MAX_FRAME_LENGTH = 1024 * 10;
    int LENGTH_FIELD_OFFSET = 0;
    int LENGTH_FIELD_LENGTH = 4;
    int LENGTH_ADJUSTMENT = 0;
    // 这样下一个Handler接收到的就不包含length了，直接就是message
    int INITIAL_BYTES_TO_STRIP = 4;
}

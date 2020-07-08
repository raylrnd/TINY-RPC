package com.example.tinyrpc.serialization;

import com.example.tinyrpc.common.extension.SPI;

/**
 * @auther zhongshunchao
 * @date 17/05/2020 16:40
 */
@SPI("fastjson")
public interface Serialization {
    <T> byte[] serialize(T obj) throws Exception;
    <T> T deserialize(byte[] data, Class<T> cls) throws Exception;
}

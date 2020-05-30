package com.example.tinyrpc.serialization;

/**
 * @auther zhongshunchao
 * @date 17/05/2020 16:40
 */
public interface Serializer {
    <T> byte[] serialize(T obj) throws Exception;
    <T> T deserialize(byte[] data, Class<T> cls) throws Exception;
}

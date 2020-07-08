package com.example.tinyrpc.serialization.impl;

import com.example.tinyrpc.serialization.Serialization;
import com.alibaba.fastjson.JSONObject;


/**
 * @auther zhongshunchao
 * @date 31/05/2020 15:33
 */
public class FastJsonSerialization implements Serialization {

    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return JSONObject.toJSONBytes(obj);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        return JSONObject.parseObject(data, cls);
    }
}

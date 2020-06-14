package com.example.tinyrpc.serialization.serializer;

import com.example.tinyrpc.serialization.Serializer;
import com.alibaba.fastjson.JSONObject;


/**
 * @auther zhongshunchao
 * @date 31/05/2020 15:33
 */
public class JsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        try {
            return JSONObject.toJSONBytes(obj);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws Exception {
        return JSONObject.parseObject(data, cls);
    }
}

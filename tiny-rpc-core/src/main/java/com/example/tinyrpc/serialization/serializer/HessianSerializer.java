package com.example.tinyrpc.serialization.serializer;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @auther zhongshunchao
 * @date 23/05/2020 14:26
 */
public class HessianSerializer implements Serializer {

    private static Logger log = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        byte[] results;
        ByteArrayOutputStream os = null;
        HessianSerializerOutput hessianOutput;
        try {
            os = new ByteArrayOutputStream();
            hessianOutput = new HessianSerializerOutput(os);
            hessianOutput.writeObject(obj);
            results = os.toByteArray();
        } catch (Exception e) {
            throw new Exception("序列化异常:{}");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {

            }
        }
        return results;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        if (data == null) {
            throw new NullPointerException();
        }
        T result;
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(data);
            HessianSerializerInput hessianInput = new HessianSerializerInput(is);
            result = cls.cast(hessianInput.readObject());
        } catch (Exception e) {
            throw new BusinessException("can not deserialize data:", JSON.toJSONString(data));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

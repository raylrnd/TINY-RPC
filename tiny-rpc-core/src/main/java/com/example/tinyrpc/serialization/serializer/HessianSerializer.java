package com.example.tinyrpc.serialization.serializer;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.example.tinyrpc.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @auther zhongshunchao
 * @date 23/05/2020 14:26
 */
public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        byte[] results = null;
        ByteArrayOutputStream os = null;
        HessianSerializerOutput hessianOutput = null;
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
    public <T> T deserialize(byte[] data, Class<T> cls) throws Exception {
        if (data == null) {
            throw new NullPointerException();
        }
        T result = null;
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(data);
            HessianSerializerInput hessianInput = new HessianSerializerInput(is);
            result = cls.cast(hessianInput.readObject());
        } catch (Exception e) {
            throw new Exception("反序列化异常:{}");
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

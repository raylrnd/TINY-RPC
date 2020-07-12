package com.example.tinyrpc.serialization.impl;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.serialization.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @auther zhongshunchao
 * @date 23/05/2020 14:26
 */
public class HessianSerialization implements Serialization {

    private static final Logger logger = LoggerFactory.getLogger(HessianSerialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream os = null;
        HessianSerializerOutput hessianOutput;
        try {
            os = new ByteArrayOutputStream();
            hessianOutput = new HessianSerializerOutput(os);
            hessianOutput.writeObject(obj);
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                logger.error("Fail to close Hessian serialization IO", e);
            }
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        if (data == null) {
            throw new NullPointerException();
        }
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(data);
            HessianSerializerInput hessianInput = new HessianSerializerInput(is);
            return cls.cast(hessianInput.readObject());
        } catch (Exception e) {
            logger.error("Can not deserialize data:", JSON.toJSONString(data));
            e.printStackTrace();
            throw new BusinessException("Can not deserialize data:" + JSON.toJSONString(data));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.error("Fail to close Hessian serialization IO", e);
            }
        }
    }
}

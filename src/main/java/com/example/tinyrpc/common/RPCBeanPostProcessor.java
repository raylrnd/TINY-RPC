package com.example.tinyrpc.common;

import com.example.tinyrpc.common.annotation.Reference;
import com.example.tinyrpc.config.GlobalConfig;
import com.example.tinyrpc.config.ReferenceConfig;
import com.example.tinyrpc.protocol.DubboInvoker;
import com.example.tinyrpc.proxy.JdkProxyFactory;
import com.example.tinyrpc.proxy.ProxyFactory;
import com.example.tinyrpc.transport.client.NettyClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 3:06 下午
 */
public class RPCBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> curClass = bean.getClass();
        Field[] fields = curClass.getDeclaredFields();
        //扫描含有@Reference的字段
        for (Field field : fields) {
            if (field.isAnnotationPresent(Reference.class)){
                Class interfaceClass = field.getType();
                Reference reference = field.getAnnotation(Reference.class);
                if (reference != null) {
                    ReferenceConfig referenceConfig = new ReferenceConfig();
                    referenceConfig.setAsync(reference.async());
                    referenceConfig.setCallback(reference.callback());
                    referenceConfig.setOneway(reference.oneway());
                    referenceConfig.setTimeout(reference.timeout());
                    referenceConfig.setSerializer(reference.serializer());
                    referenceConfig.setProxy(reference.proxy());
                    //将含有@Reference的字段的属性替换成代理对象
                    try {
                        field.setAccessible(true);
                        field.set(bean, referenceConfig.getProxyFactory().createProxy(new DubboInvoker(interfaceClass, new NettyClient())));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                return bean;
            }
        }
        return null;
    }
}

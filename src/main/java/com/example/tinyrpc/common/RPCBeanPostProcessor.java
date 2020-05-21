package com.example.tinyrpc.common;

import com.example.tinyrpc.common.annotation.Reference;
import com.example.tinyrpc.proxy.JdkProxyFactory;
import com.example.tinyrpc.proxy.ProxyFactory;
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
                Class clazz= (Class) field.getGenericType();
                ProxyFactory proxy = new JdkProxyFactory();
                proxy.createProxy()
                //实现替换
                try {
                    field.setAccessible(true);
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return bean;
            }
        }
        return null;
    }
}

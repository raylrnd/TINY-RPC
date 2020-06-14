package com.example.autoconfig.annotation;


import com.example.tinyrpc.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


/**
 * @auther zhongshunchao
 * @date 29/05/2020 09:04
 */
//解析@Providdr,将该类添加至缓存
public class MyServiceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (!beanClass.isAnnotationPresent(MyService.class)) {
            return bean;
        }
        MyService rpcService = beanClass.getAnnotation(MyService.class);
        Class<?> interfaceClass = rpcService.interfaceClass();
        ServiceConfig.SERVICE_MAP.put(interfaceClass.getName(), bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}

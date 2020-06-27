package com.example.autoconfig.annotation;

import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

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
        URL url = new URL();
        String interfaceName = rpcService.interfaceClass().getName();
        url.setInterfaceName(interfaceName);
        url.setPort(rpcService.port());
        url.setWeight(rpcService.weight());
        url.setIp("127.0.0.1");
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.export(url, bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}

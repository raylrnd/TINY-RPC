package com.example.autoconfig.annotation;

import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import static com.example.tinyrpc.common.Constants.LOCAL_HOST;
import static com.example.tinyrpc.common.utils.SerializerUtil.SERIALIZER_MAP;

/**
 * @auther zhongshunchao
 * @date 29/05/2020 09:04
 */
//解析@Providdr,将该类添加至缓存
public class ProviderBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (!beanClass.isAnnotationPresent(Provider.class)) {
            return bean;
        }
        Provider rpcService = beanClass.getAnnotation(Provider.class);
        URL url = new URL();
        String interfaceName = rpcService.interfaceClass().getName();
        url.setInterfaceName(interfaceName).setPort(rpcService.port()).setWeight(rpcService.weight())
                .setIp(LOCAL_HOST).setAddress(LOCAL_HOST + ":" + rpcService.port())
                .setProtocol(rpcService.protocol()).setFilters(rpcService.filter()).setProxy(rpcService.proxy())
                .setSerializer(SERIALIZER_MAP.get(rpcService.serializer())).setRegistry(rpcService.registry());
        ServiceConfig serviceConfig = new ServiceConfig();
        url.setRef(bean);
        serviceConfig.export(url);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}

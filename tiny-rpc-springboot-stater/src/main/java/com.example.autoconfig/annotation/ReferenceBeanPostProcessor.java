package com.example.autoconfig.annotation;


import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.config.ReferenceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

import static com.example.tinyrpc.common.utils.SerializerUtil.SERIALIZER_MAP;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 3:06 下午
 */
//解析@Reference
public class ReferenceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

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
                    Invocation invocation = new Invocation();
                    URL url = new URL();
                    url.setOneWay(reference.oneway()).setTimeout(reference.timeout()).setProtocol(reference.protocol())
                            .setProxy(reference.proxy()).setLoadbalance(reference.loadbalance())
                            .setSerializer(SERIALIZER_MAP.get(reference.serializer())).setFilters(reference.filter());
                    invocation.setInterfaceClass(interfaceClass);
                    invocation.setServiceName(interfaceClass.getName());
                    invocation.setUrl(url);
                    //将含有@Reference的字段的属性替换成代理对象
                    try {
                        field.setAccessible(true);
                        ReferenceConfig referenceConfig = new ReferenceConfig();
                        Object proxy = referenceConfig.getProxy(invocation);
                        field.set(bean, proxy);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bean;
    }


}

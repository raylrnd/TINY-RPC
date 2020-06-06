package com.example.tinyrpc.common.annotation;


import com.example.tinyrpc.config.ReferenceConfig;
import com.example.tinyrpc.proxy.JdkProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


import java.lang.reflect.Field;

import static com.example.tinyrpc.common.utils.SerializerUtil.SERIALIZER_MAP;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 3:06 下午
 */
//解析@Reference
@Component
public class MyReferenceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> curClass = bean.getClass();
        Field[] fields = curClass.getDeclaredFields();
        //扫描含有@Reference的字段
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyReference.class)){
                Class interfaceClass = field.getType();
                MyReference reference = field.getAnnotation(MyReference.class);
                if (reference != null) {
                    ReferenceConfig referenceConfig = new ReferenceConfig();
                    referenceConfig.setAsync(reference.async());
                    referenceConfig.setCallback(reference.callback());
                    referenceConfig.setOneway(reference.oneway());
                    referenceConfig.setTimeout(reference.timeout());
                    referenceConfig.setSerializer(SERIALIZER_MAP.get(reference.serializer()));
                    referenceConfig.setProxy(reference.proxy());
                    //将含有@Reference的字段的属性替换成代理对象
                    try {
                        field.setAccessible(true);
                        JdkProxyFactory jdkProxyFactory = new JdkProxyFactory(interfaceClass, referenceConfig);
                        Object proxy = jdkProxyFactory.createProxy();
                        field.set(bean, proxy);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bean;
    }


}

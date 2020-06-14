package com.example.autoconfig;

import com.example.autoconfig.annotation.MyReferenceBeanPostProcessor;
import com.example.autoconfig.annotation.MyServiceBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther zhongshunchao
 * @date 14/06/2020 09:35
 */
@Configuration
public class MyAutoConfiguration {

    @Bean
    public MyReferenceBeanPostProcessor myReferenceBeanPostProcessor() {
        return new MyReferenceBeanPostProcessor();
    }

    @Bean
    public MyServiceBeanPostProcessor myServiceBeanPostProcessor() {
        return new MyServiceBeanPostProcessor();
    }
}

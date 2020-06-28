package com.example.autoconfig;

import com.example.autoconfig.annotation.ReferenceBeanPostProcessor;
import com.example.autoconfig.annotation.ProviderBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther zhongshunchao
 * @date 14/06/2020 09:35
 */
@Configuration
public class MyAutoConfiguration {

    @Bean
    public ReferenceBeanPostProcessor myReferenceBeanPostProcessor() {
        return new ReferenceBeanPostProcessor();
    }

    @Bean
    public ProviderBeanPostProcessor myServiceBeanPostProcessor() {
        return new ProviderBeanPostProcessor();
    }
}

package com.example.tinyrpc.config;

import java.util.HashMap;

/**
 * @auther zhongshunchao
 * @date 29/05/2020 09:07
 */
public class ServiceConfig {
    Class<?> interfaceClass;
    public static final HashMap<String, Object> SERVICE_MAP = new HashMap<>();

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

}

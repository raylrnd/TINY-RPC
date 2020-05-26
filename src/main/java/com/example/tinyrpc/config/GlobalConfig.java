package com.example.tinyrpc.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @auther zhongshunchao
 * @date 23/05/2020 10:33
 */
@Deprecated
@Component
@ConfigurationProperties(prefix = "tinyrpc")
public class GlobalConfig {

//    private static String serialize;
//
//    private static String proxy;

    //采用的协议
    private static String protocol;


//    @Value("serialize.type")
//    public void setSerialize(String serialize) {
//        this.serialize = serialize;
//    }
//    @Value("proxy.type")
//    public void setProxy(String proxy) {
//        this.proxy = proxy;
//    }
    @Value("protocol.type")
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

//    public static final ProxyFactory PROXY_FACTORY = getProxyFactory();
//    public static final Serializer SERIALIZER = getSerializer();

//    public static ProxyFactory getProxyFactory() {
//        switch (proxy) {
//            case "jdk":
//                return new JdkProxyFactory();
//            case "javassist":
//                return new JavassistProxyFactory();
//        }
//        return null;
//    }
//
//    public static Serializer getSerializer() {
//        switch (serialize) {
//            case "hessian":
//                return new HessianSerializer();
//            case "protobuff":
//                return new ProtostuffSerializer();
//        }
//        return null;
//    }


}

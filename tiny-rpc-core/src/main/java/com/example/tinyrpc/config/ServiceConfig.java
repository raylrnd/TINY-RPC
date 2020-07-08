package com.example.tinyrpc.config;

import com.example.tinyrpc.common.extension.ExtensionLoader;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 29/05/2020 09:07
 */
public class ServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConfig.class);

    public static final Map<String, Invoker> INVOKER_MAP = new ConcurrentHashMap<>();

    //将服务发布到ZK并开启server
    public void export(URL url) {
        Protocol protocol = ExtensionLoader.getExtensionLoader().getExtension( Protocol.class, url.getProtocol());
        protocol.export(url);
    }
}

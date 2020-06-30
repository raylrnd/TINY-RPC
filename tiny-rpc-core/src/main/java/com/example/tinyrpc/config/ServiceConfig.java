package com.example.tinyrpc.config;

import com.example.tinyrpc.common.ExtensionLoader;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @auther zhongshunchao
 * @date 29/05/2020 09:07
 */
public class ServiceConfig {

    private static Logger log = LoggerFactory.getLogger(ServiceConfig.class);

    public static final HashMap<String, Object> SERVICE_MAP = new HashMap<>();

    //将服务发布到ZK并开启server
    public void export(URL url, Object bean) {
        Protocol protocol = ExtensionLoader.getExtensionLoader().getExtension( Protocol.class, url.getProtocol());
        protocol.export(url);
        ServiceConfig.SERVICE_MAP.put(url.getInterfaceName(), bean);
    }

}

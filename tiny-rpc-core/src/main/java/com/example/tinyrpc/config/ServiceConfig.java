package com.example.tinyrpc.config;

import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.protocol.RegistryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @auther zhongshunchao
 * @date 29/05/2020 09:07
 */
public class ServiceConfig<T> {

    private static Logger log = LoggerFactory.getLogger(ServiceConfig.class);

    private URL url;

    private volatile T ref;

    public static final HashMap<String, Object> SERVICE_MAP = new HashMap<>();

    private static final Protocol REF_PROTOCOL = new RegistryProtocol();

//    public ServiceConfig(String interfaceName, T ref, int port) {
//        this.interfaceName = interfaceName;
//        this.ref = ref;
//        this.port = port;
//        export();
//    }

    public ServiceConfig(URL url, T ref) {
        this.url = url;
        this.ref = ref;
    }


    //将服务发布到ZK并开启server
    private void export() {
        REF_PROTOCOL.export(url);


    }

}

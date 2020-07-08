package com.example.tinyrpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther zhongshunchao
 * @date 05/07/2020 15:31
 */
public class GlobalConfig {

    private static final Logger logger = LoggerFactory.getLogger(GlobalConfig.class);

    private String appName;

    private int threadsNum;

    public static Map<Long, Object> callbackMap = new HashMap<>();

    private GlobalConfig() {
    }

    private void loadGlobalConfig() {

    }

    private static volatile GlobalConfig globalConfig;

    public static GlobalConfig getGlobalConfig() {
        if (globalConfig == null) {
            synchronized (GlobalConfig.class) {
                if (globalConfig == null) {
                    globalConfig = new GlobalConfig();
                }
            }
        }
        return globalConfig;
    }


    public static void saveCallBack(long requestId, Object callback) {
        callbackMap.put(requestId, callback);
    }

    public static Object getAndRemoveCallBack(long requestId) {
        return callbackMap.remove(requestId);
    }
}

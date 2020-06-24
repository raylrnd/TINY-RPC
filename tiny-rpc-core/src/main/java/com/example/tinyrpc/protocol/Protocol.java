package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.transport.Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Dubbo中的export()是将invoker转化为Exporter, DubboExporter持有exporterMap对象，该exporterMap可以理解为是当前的invokerList，使用者可以通过export方法得到Exporter，进而得到当前的invokerList集合
 * 我这的export()是将接口信息（接口全限定名 -> ip:port）注册到Zookeeper中，然后开启Server
 * 将Invoker添加进InvokerMap中，然后注册到ZK中，然后开启Server。Invoker封装了接口的实现类
 * 可以参考Dubbo的服务暴露：https://www.jianshu.com/p/30011f94ec24
 * @auther zhongshunchao
 * @date 13/06/2020 21:15
 */
public interface Protocol {

    Invoker refer(Class<?> interfaceClass);

    void export(URL url);
}

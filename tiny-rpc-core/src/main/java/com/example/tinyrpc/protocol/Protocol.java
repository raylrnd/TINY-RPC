package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.extension.SPI;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.transport.Endpoint;

/**
 * @auther zhongshunchao
 * @date 13/06/2020 21:15
 * Dubbo中的export()是将invoker转化为Exporter, DubboExporter持有exporterMap对象，该exporterMap可以理解为是当前的invokerList，使用者可以通过export方法得到Exporter，进而得到当前的invokerList集合
 * 可以参考Dubbo的服务暴露：https://www.jianshu.com/p/30011f94ec24
 */
@SPI("TINY-RPC")
public interface Protocol {

    /**
     * 引用服务
     * @param invocation
     * @return 返回Filter链中的最后一个Invoker，即RealInvoker
     */
    Invoker refer(Invocation invocation);

    /**
     * 将接口信息（接口全限定名 -> ip:port）注册到Zookeeper中，然后开启Server
     * @param url
     */
    Endpoint export(URL url);
}

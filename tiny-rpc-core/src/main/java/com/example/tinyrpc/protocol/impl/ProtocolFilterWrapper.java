package com.example.tinyrpc.protocol.impl;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.domain.Constants;
import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.common.extension.ExtensionLoader;
import com.example.tinyrpc.config.ServiceConfig;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.transport.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 该类为被SPI加载的扩展点，负责包装RegistryProtocol，以及生产InvokerChain
 * @auther zhongshunchao
 * @date 27/06/2020 20:29
 */
public class ProtocolFilterWrapper implements Protocol {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolFilterWrapper.class);

    private static final Protocol PROTOCOL = new RegistryProtocol();

    private URL url;

    /**
     * 返回责任链头部的Filter
     * @param invoker
     * @return
     */
    private Invoker buildInvokerChain(final Invoker invoker, int side) {
        Invoker last = invoker;
        // 获得所有激活的Filter(简化处理，不进行排序)
        List<Filter> filters = ExtensionLoader.getExtensionLoader().buidFilterChain(url.getFilters(), side);
        logger.info("Build Filter Successful, Filters:{}", JSON.toJSONString(filters));
        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                final Filter filter = filters.get(i);
                // 复制引用，构建filter调用链
                final Invoker next = last;
                // 这里只是构造一个最简化的Invoker作为调用链的载体Invoker
                last = new Invoker() {
                    @Override
                    public URL getUrl() {
                        return invoker.getUrl();
                    }

                    @Override
                    public Class<?> getInterface() {
                        return invoker.getInterface();
                    }

                    @Override
                    public Object invoke(Invocation invocation) throws Exception {
                        return filter.invoke(next, invocation);
                    }

                    @Override
                    public void destroy() {
                        invoker.destroy();
                    }
                };
            }
        }
        return last;
    }


    @Override
    public Invoker refer(Invocation invocation) {
        this.url = invocation.getUrl();
        // 进行负载均衡之后得到invoker
        Invoker invoker;
        String serviceName = invocation.getServiceName();
        if (invocation.isInjvm() && ServiceConfig.INVOKER_MAP.containsKey(serviceName)) {
            return ServiceConfig.INVOKER_MAP.get(serviceName);
        } else {
            // 构建invoker chain
            invoker = PROTOCOL.refer(invocation);
            return buildInvokerChain(invoker, Constants.CLIENT_SIDE);
        }
    }

    @Override
    public Endpoint export(URL url) {
        this.url = url;
        Endpoint endpoint = PROTOCOL.export(url);
        RealInvoker invoker = new RealInvoker(null, 0, url);
        invoker.setRef(url.getRef());
        invoker.setEndpoint(endpoint);
        ServiceConfig.INVOKER_MAP.put(url.getInterfaceName(), buildInvokerChain(invoker, Constants.SERVER_SIDE));
        return endpoint;
    }
}

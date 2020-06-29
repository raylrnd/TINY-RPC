package com.example.tinyrpc.protocol.impl;

import com.example.tinyrpc.common.ExtensionLoader;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * @auther zhongshunchao
 * @date 27/06/2020 20:29
 */
public class ProtocolFilterWrapper implements Protocol {

    private static Logger log = LoggerFactory.getLogger(ProtocolFilterWrapper.class);

    private final Protocol protocol;

    private URL url;

    public ProtocolFilterWrapper() {
        this.protocol = new RegistryProtocol();
    }

    private Invoker buildInvokerChain(final Invoker invoker) {
        Invoker last = invoker;
        // 获得所有激活的Filter(简化处理，不进行排序)
        List<Filter> filters = ExtensionLoader.buidFilterChain(url.getFilters());
        log.info("Build Filter Successful, Filters:{}", filters);
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
                    public Object invoke(Invocation invocation) {
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
        return buildInvokerChain(protocol.refer(invocation));
    }

    //为了简化设计，只处理consumer端，不处理provider端
    @Override
    public void export(URL url) {
        protocol.export(url);
    }
}

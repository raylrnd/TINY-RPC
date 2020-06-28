package com.example.tinyrpc.common;

/**
 * @auther zhongshunchao
 * @date 2020/5/19 6:30 下午
 */
// 封装对方发送过来的request请求
public class Invocation {

    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private Attachments attachments;

    private transient Class<?> interfaceClass;

    public static class Attachments {

        private boolean event = false;

        private boolean oneWay = false;

        private transient long timeout;

        private transient int serializer;

        private String protocol;

        private String proxy;

        private String loadbalance;

        private String[] filters;

        public boolean isEvent() {
            return event;
        }

        public Attachments setEvent(boolean event) {
            this.event = event;
            return this;
        }

        public boolean isOneWay() {
            return oneWay;
        }

        public Attachments setOneWay(boolean oneWay) {
            this.oneWay = oneWay;
            return this;
        }

        public long getTimeout() {
            return timeout;
        }

        public Attachments setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public int getSerializer() {
            return serializer;
        }

        public Attachments setSerializer(int serializer) {
            this.serializer = serializer;
            return this;
        }

        public String getProtocol() {
            return protocol;
        }

        public Attachments setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public String getProxy() {
            return proxy;
        }

        public Attachments setProxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        public String getLoadbalance() {
            return loadbalance;
        }

        public Attachments setLoadbalance(String loadbalance) {
            this.loadbalance = loadbalance;
            return this;
        }

        public String[] getFilters() {
            return filters;
        }

        public Attachments setFilters(String[] filters) {
            this.filters = filters;
            return this;
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }


    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }
}

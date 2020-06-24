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

    private boolean event = false;

    private boolean oneWay = false;

    private transient long timeout;

    private transient int serializer;

    private transient Class<?> interfaceClass;

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
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

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getSerializer() {
        return serializer;
    }

    public void setSerializer(int serializer) {
        this.serializer = serializer;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}

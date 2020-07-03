package com.example.tinyrpc.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @auther zhongshunchao
 * @date 2020/5/19 6:30 下午
 */
// 封装对方发送过来的request请求
public class Invocation implements Serializable {

    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private Map<String, String> attachments;

    private transient boolean isClientSide;

    private transient int timeout = 1000;

    private transient URL url;

    private transient Class<?> interfaceClass;

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

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isClientSide() {
        return isClientSide;
    }

    public void setClientSide(boolean clientSide) {
        isClientSide = clientSide;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invocation)) return false;
        Invocation that = (Invocation) o;
        return getServiceName().equals(that.getServiceName()) &&
                getMethodName().equals(that.getMethodName()) &&
                Arrays.equals(getParameterTypes(), that.getParameterTypes()) &&
                Arrays.equals(getArguments(), that.getArguments()) &&
                getUrl().equals(that.getUrl()) &&
                getInterfaceClass().equals(that.getInterfaceClass());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getServiceName(), getMethodName(), getUrl(), getInterfaceClass());
        result = 31 * result + Arrays.hashCode(getParameterTypes());
        result = 31 * result + Arrays.hashCode(getArguments());
        return result;
    }

}

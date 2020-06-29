package com.example.tinyrpc.common;

/**
 * @auther zhongshunchao
 * @date 24/06/2020 18:24
 * dubbo中的URL中定义了一些扩展点的实现名、应用名、方法名，所属类目、版本号等
 */
public class URL {

    private String interfaceName;

    private int weight;

    private String ip;

    private int port;

    private String address;

    private boolean event = false;

    private boolean oneWay = false;

    private long timeout;

    private int serializer;

    private String protocol;

    private String proxy;

    private String loadbalance;

    private String[] filters;

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public URL setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public URL setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getSerializer() {
        return serializer;
    }

    public URL setSerializer(int serializer) {
        this.serializer = serializer;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public URL setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getProxy() {
        return proxy;
    }

    public URL setProxy(String proxy) {
        this.proxy = proxy;
        return this;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public URL setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
        return this;
    }

    public String[] getFilters() {
        return filters;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String exposeURL() {
        return getAddress() + "&" + weight;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}

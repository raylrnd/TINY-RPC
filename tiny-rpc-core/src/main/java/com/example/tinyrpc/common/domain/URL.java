package com.example.tinyrpc.common.domain;

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

    private boolean aync = false;

    private long timeout;

    private int actives;

    private String serialization;

    private String protocol;

    private String proxy;

    private String loadbalance;

    private String[] filters;

    private String registry;

    private Object ref;

    private int threads;

    private int queues;

    private String threadFactoryName;

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

    public boolean isAync() {
        return aync;
    }

    public URL setAync(boolean aync) {
        this.aync = aync;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public URL setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getActives() {
        return actives;
    }

    public URL setActives(int actives) {
        this.actives = actives;
        return this;
    }

    public String getSerialization() {
        return serialization;
    }

    public URL setSerialization(String serialization) {
        this.serialization = serialization;
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

    public URL setFilters(String[] filters) {
        this.filters = filters;
        return this;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public URL setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public URL setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public URL setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public URL setPort(int port) {
        this.port = port;
        return this;
    }

    public String exposeURL() {
        return getAddress() + "&" + weight;
    }


    public URL setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public String getRegistry() {
        return registry;
    }

    public URL setRegistry(String registry) {
        this.registry = registry;
        return this;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getQueues() {
        return queues;
    }

    public void setQueues(int queues) {
        this.queues = queues;
    }

    public String getThreadFactoryName() {
        return threadFactoryName;
    }

    public void setThreadFactoryName(String threadFactoryName) {
        this.threadFactoryName = threadFactoryName;
    }

    public String toIdentityString() {
        return address + "/" + interfaceName;
    }
}

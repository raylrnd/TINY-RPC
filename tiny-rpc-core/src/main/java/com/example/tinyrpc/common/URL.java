package com.example.tinyrpc.common;

/**
 * @auther zhongshunchao
 * @date 24/06/2020 18:24
 */
//dubbo中的URL中定义了一些扩展点的实现名、应用名、方法名，所属类目、版本号等
public class URL {

    private String interfaceName;

    private int weight;

    private String ip;

    private int port;

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
        return ip + ":" + port + "$" + weight;
    }
}

# TINY-RPC
TINY-RPC  
基于Netty的小型RPC项目  
目前为version_2.0版本，java代码总计约3500行  
里程碑，总工时45pd   
项目文档记录在了我的个人博客：http://www.shunchao.ink/tiny-rpc/

 |                             功能点                                  |  完成时间   |
 | :----------------------------------------------------------:       | :--------: |
 |                      实现基本的RPC通信                               | 2020.06.06 |
 |     添加 SpringBoot Stater、实现了类Dubbo协议                         | 2020.06.14 |
 | 添加Zookeeper，提供服务发现和注册, 添加负载均衡策略LoadBalancer           | 2020.06.27 |
 |                     添加ExtensionLoader、@SPI注解                   | 2020.06.30 |
 |            添加同步、异步调用方式、添加业务线程池                         | 2020.07.03 |
 |                添加Filter，实现链路跟踪和限流                          | 2020.07.04 |
 |                    支持多种负载均衡策略                                | 2020.07.05 |
 

Zookeeper中的节点格式为

| path                                   | value                |
| :--------------------------------------: | :--------------------: |
| /TINY-RPC/com.example.tinyrpc.AService | 192.168.1.1:1221&100 |
| /TINY-RPC/com.example.tinyrpc.BService | 192.168.1.4:1221&200 |
| /TINY-RPC/com.example.tinyrpc.AService | 192.168.1.2:1221&300 |



   由于本人临近毕业时间仓促，项目只能暂时进行到这。后面有时间会补上高并发测试和一些想实现但目前尚未实现的功能点。  
   
   | 未来的Feature                                        |
   | ---------------------------------------------------- |
   | 添加Netty长连接，添加心跳策略，实现断线重连          |
   | 添加javassist，代替原有的JDK动态代理，减少反射开销 |
   | 添加FailOver,FailFast等多种集群容错                  |
   | 支持callback回调                                     |
   | 实现多种协议，如HTTP                                 |
  -----
    
参考书籍：
《深入理解Apache Dubbo与实践》、《深度剖析Apache Dubbo核心技术内幕》  
个人博客:[shunchao.ink](http://www.shunchao.ink)  

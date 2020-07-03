# TINY-RPC
TINY-RPC  
基于Netty的小型RPC项目  
目前为version_1.1版本  
|功能列表|  
|version1.0 : 仅仅为可运行的版本  (Done 2020.06.06)    
|version1.1 : 为TINY-RPC 添加 SpringBoot Stater  (Done 2020.06.14)  
|version1.2 : 添加Zookeeper，提供服务发现和注册, 添加负载均衡策略LoadBalancer  (Done 2020.06.27)  
|version1.3 : 添加ExtensionLoader (Done 2020.06.30)     
|version1.4 : 添加同步、异步调用方式、添加业务线程池  
|version2.0 : 添加Filter，实现链路跟踪和限流   
How to Run:    
在测试仓库[https://github.com/raylrnd/tiny-rpc-sample.git](https://github.com/raylrnd/tiny-rpc-sample.git)中分别运行'ServerDemo'和'ClientDemo'  
version1.2 运行效果图  
客户端效果图
![image](https://upload-images.jianshu.io/upload_images/16081207-b3a03e4f80c366af.png)  
服务端效果图
![image](https://upload-images.jianshu.io/upload_images/16081207-3c0abbcb20799619.png) 

个人博客:[shunchao.ink](http://www.shunchao.ink)

version1.2实现思路：
生产者：  
准备阶段：  
容器初始化阶段：解析@MyService注解，将服务和端口发布到ZK上，开启Server  
真实调用阶段：直接用反射方法调用，为什么要有invokerMap。  
服务端和客户端的invoker有什么不同  

1.1版本中服务端是根据消费端传过来的接口的全限定名根据反射来调用得到结果，反射会影响性能，且无法支持一个接口的多种实现类的调用方式  
那么这里应该优化一下，在解析@MyService注解的时候应该将接口的实现类先生成出来放入Invoker进行包装，然后放入到一个Map里面。

消费者：  
容器初始化阶段：@Reference注解，根据接口名去zk里找到server的ip和端口号，根据ip+端口号创建Client，等Client连接成功。生成一个ClassName->Client#sent()的引用map :: clientMap。如果一个服务由多个ip提供，该选哪个？  
真实调用阶段：动态代理原理就是用反射生成了一个代理类，根据要代理的接口去构造自己的方法。然后调用代理类的invoke方法，该方法会在clientMap中找到对应的Client 

关键点是找到IP和端口号，然后发送数据，等待接收数据，得到调用结果。这里面有优化的点，可以将Client缓存起来。 

version1.3  
在客户端添加LoadBalance和Filter，从InvokerList里面select出要调用的Invoker。根据权重、随机等，要先实现Filter，获取负载均衡参数，Filter可以采用Pipline模式，参考Pigeon  

本项目的Zookeeper目录格式为 ： 
|path                                   |  value               |  
|/TINY-RPC/com.example.tinyrpc.AService | 192.168.1.1:1221$100 |  
|/TINY-RPC/com.example.tinyrpc.BService | 192.168.1.4:1221$200 |  
|/TINY-RPC/com.example.tinyrpc.AService | 192.168.1.2:1221&300 |  

参考书籍：
《深入理解Apache Dubbo与实践》、《深度剖析Apache Dubbo核心技术内幕》  
个人博客:[shunchao.ink](http://www.shunchao.ink)

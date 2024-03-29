# RuayouGateway网关

该项目是基于Netty搭建的高性能微服务网关项目，整合Nacos作为服务的注册中心和配置中心，最细可配置接口粒度的路由规则，可实时动态热更新路由规则以及系统的属性配置。具有 IP、IP段黑名单，Mock 模拟，灰度发布，流量控制，负载均衡，路由分发，超时重试等功能。

## 技术栈

Netty，SpringBoot，Asynchttp，Nacos，Caffeine Cache，Disruptor，Java SPI

## 项目核心亮点：

- 采用"微内核+插件"的架构，网关的大部分组件，如过滤器，负载均衡策略等都能够便利地实现扩展、增强、替换。
- 开发客户端的 Starter，实现导入依赖自动完成服务注册。
- 实现接口级别的路由规则配置，以及路由规则热更新。
- 使用无锁高性能队列 Disruptor 对网关进行性能优化。
- 网关内部大量使用缓存，采用事件派发机制保证规则热更新时的缓存一致性，提高响应速度。
- 项目使用大量设计模式来提高代码的扩展性。例如责任链，策略，观察者，适配器，工厂，单例模式等。



## 使用以及配置

### 配置文件

**核心配置**

````yaml
httpClientConfig:
  httpConnectTimeout: 30000
  httpConnectionsPerHost: 8000
  httpMaxConnections: 10000
  httpMaxRequestRetry: 2
  httpPooledConnectionIdleTimeout: 60000
  httpRequestTimeout: 30000
nettyServerConfig:
  eventLoopGroupWorkerNum: 16
  maxContentLength: 67108864
  port: 8999
````

**路由规则配置**
rules是map结构。可配置多个路由规则，每个规则也可以手动指定适用多个服务。

- 需要手动指定开启哪些过滤器。
- 限流可以根据路径限流或者是根据服务限流。
- 黑名单限制，可以根据IP或者IP段进行限制。
- 负载均衡，现在实现的算法有：随机，轮询。
- Mock模拟，可以根据路径配置模拟响应数据

````yaml
rules:
  '1':
    filters:
    - load_balance_filter
    # - mock_filter
    # - flow_ctl_filter
    # - limit_filter
    flowControlConfigs:
    - flowRule:
        ping-http-server: 100000
      id: serviceflow
      model: local
      order: 1
      serviceIds:
      - ping-http-server
      - user-server
      type: service
    limitConfig:
      type: ip
      limitRule:
      # - 192.168.8.113-192.168.8.120
    loadBalanceConfig:
      strategy: Polling
    mockConfig:
      mockMap:
        # POST:/ping/mb/nb: mockdate
        # POST:/user/hello: 你好ya！
    order: 1
    patterns:
      /ping/*: ping-http-server
      /user/*: user-server
    protocol: http
    retryConfig:
      retryCount: 3
    ruleId: '1'
    serviceIds:
#适配这套路由规则的服务id
    - user-server
    - ping-http-server
    version: 1.0.0
````



### 客户端使用

通过maven坐标引入客户端
````xml
        <dependency>
            <groupId>com.ruayou</groupId>
            <artifactId>RuayouGateway-Client</artifactId>
            <version>1.0</version>
        </dependency>
````

之后可以在任意一个Spring组件上使用注解配置服务信息,然后配置文件中配置注册中心相关信息，就能够启动服务并注册到网关。

![image-20240316211223093](https://cdn.jsdelivr.net/gh/YuanErya/pictures@main/img/202403162112137.png)

````yaml
register:
  address: 192.168.8.111:8848
  env: dev
  gray: false
````

之后可以在配置中心，写路由规则，当然不写也会有一套默认的路由规则，只开启了负载均衡和路由转发。



## 吞吐量

Mock模拟吞吐量
![image-20240313141407555](https://cdn.jsdelivr.net/gh/YuanErya/pictures@main/img/202403131414604.png)

简单服务转发吞吐量

![image-20240316203814338](https://cdn.jsdelivr.net/gh/YuanErya/pictures@main/img/202403162038482.png)
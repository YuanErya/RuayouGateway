package com.ruayou.core;

import com.alibaba.fastjson2.JSON;
import com.ruayou.common.api_interface.register_center.RegisterCenter;
import com.ruayou.common.api_interface.register_center.RegisterCenterListener;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.utils.NetUtils;
import com.ruayou.core.httpclient.AsyncHttpCoreClient;
import com.ruayou.core.httpclient.HttpClientConfig;
import com.ruayou.core.netty.NettyHttpServer;
import com.ruayou.core.netty.NettyServerConfig;
import com.ruayou.core.netty.processor.HttpProcessor;
import com.ruayou.core.netty.processor.HttpServerCoreProcessor;
import com.ruayou.register_center.nacosimpl.NacosRegisterCenter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：ruayou
 * @Date：2024/1/26 19:04
 * @Filename：ServerContainer
 */
@Log4j2
public class ServerContainer implements LifeCycle{
    private final NettyServerConfig nettyServerconfig;
    private final HttpClientConfig httpClientConfig;
    private HttpProcessor processor;
    boolean initFlag=false;

    private static final  ArrayList<LifeCycle> run=new ArrayList<>();
    public ServerContainer(NettyServerConfig nettyServerconfig, HttpClientConfig httpClientConfig) {
        this.nettyServerconfig = nettyServerconfig;
        this.httpClientConfig=httpClientConfig;
    }
    @Override
    public void init() {
        if (initFlag) {
            return;
        }
        HttpServerCoreProcessor coreProcessor=new HttpServerCoreProcessor();
        this.processor=coreProcessor;
        run.add(processor);
        NettyHttpServer server = new NettyHttpServer(nettyServerconfig, processor);
        run.add(server);
        run.add(new AsyncHttpCoreClient(httpClientConfig,server.getWorkerEventLoopGroup()));
        run.forEach(LifeCycle::init);
    }

    @Override
    public void start() {
        if (!initFlag) {
            init();
        }
        run.forEach(LifeCycle::start);
        RegisterCenter registerCenter = registerAndSubscribe(nettyServerconfig);
        log.info("RuayouGateway网关启动成功，正在监听端口：{}", this.nettyServerconfig.getPort());
    }

    @Override
    public void close() {
        run.forEach(LifeCycle::close);
        log.info("========RuayouGateway已停止运行！========");
    }
    public static void addComponent(LifeCycle component){
        run.add(component);
    }

    public static List<LifeCycle> getComponents(){
        return run;
    }



    private static RegisterCenter registerAndSubscribe(NettyServerConfig config) {
        final RegisterCenter registerCenter = new NacosRegisterCenter();
        registerCenter.init(config.getRegistryAddress(), config.getEnv());
        //构造网关服务定义和服务实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(config);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(config);
        //注册
        registerCenter.register(serviceDefinition, serviceInstance);
        //订阅
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                log.info("refresh service and instance: {} {}", serviceDefinition.getUniqueId(),
                        JSON.toJSON(serviceInstanceSet));
                //要做的是把变更的新的获取到的新的实例的列表重新保存
//                DynamicConfigManager manager = DynamicConfigManager.getInstance();
//                //将这次变更事件影响之后的服务实例再次添加到对应的服务实例集合
//                manager.addServiceInstance(serviceDefinition.getUniqueId(), serviceInstanceSet);
//                //修改发生对应的服务定义
//                manager.putServiceDefinition(serviceDefinition.getUniqueId(),serviceDefinition);
            }
        });
        return registerCenter;
    }

    private static ServiceInstance buildGatewayServiceInstance(NettyServerConfig config) {
        String localIp = NetUtils.getLocalIp();
        int port = config.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + ":" + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(System.currentTimeMillis());
        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(NettyServerConfig config) {
        String applicationName = config.getApplicationName();
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setInvokerMap(Map.of());
        serviceDefinition.setUniqueId(applicationName);
        serviceDefinition.setServiceId(applicationName);
        serviceDefinition.setEnvType(config.getEnv());
        return serviceDefinition;
    }
}

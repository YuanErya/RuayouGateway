package com.ruayou.core;

import com.alibaba.fastjson.JSON;
import com.ruayou.common.api_interface.register_center.RegisterCenter;
import com.ruayou.common.api_interface.register_center.RegisterCenterListener;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.core.httpclient.HttpClientConfig;
import com.ruayou.core.netty.NettyServerConfig;
import com.ruayou.register_center.nacosimpl.NacosRegisterCenter;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @Author：ruayou
 * @Date：2024/1/2 18:34
 * @Filename：Application
 * 程序入口
 */
@Log4j2
public class Application {
    public static void main(String[] args) {

        RegisterCenter registerCenter = registerAndSubscribe(new Config());
        ServerContainer container = new ServerContainer(new NettyServerConfig(), new HttpClientConfig());
        container.start();
    }


    private static RegisterCenter registerAndSubscribe(Config config) {
        final RegisterCenter registerCenter = new NacosRegisterCenter();
        registerCenter.init("localhost:8848", config.getEnv());

        //构造网关服务定义和服务实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(config);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(config);

        //注册
        registerCenter.register(serviceDefinition, serviceInstance);

        //订阅
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                log.info("监听到变更事件！");
                log.info(serviceDefinition);
                log.info(serviceInstanceSet);
//                log.info("refresh service and instance: {} {}", serviceDefinition.getUniqueId(),
//                        JSON.toJSON(serviceInstanceSet));
//                DynamicConfigManager manager = DynamicConfigManager.getInstance();
//                //将这次变更事件影响之后的服务实例再次添加到对应的服务实例集合
//                manager.addServiceInstance(serviceDefinition.getUniqueId(), serviceInstanceSet);
//                //修改发生对应的服务定义
//                manager.putServiceDefinition(serviceDefinition.getUniqueId(),serviceDefinition);
            }
        });
        return registerCenter;
    }

    private static ServiceInstance buildGatewayServiceInstance(Config config) {
        String localIp = "localhost";
        int port = config.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + ":" + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(System.currentTimeMillis());
        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(Config config) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setInvokerMap(Map.of());
        serviceDefinition.setUniqueId(config.getApplicationName());
        serviceDefinition.setServiceId(config.getApplicationName());
        serviceDefinition.setEnvType(config.getEnv());
        return serviceDefinition;
    }
}

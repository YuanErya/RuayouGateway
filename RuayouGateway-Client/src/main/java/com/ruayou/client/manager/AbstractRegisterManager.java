package com.ruayou.client.manager;

import com.ruayou.client.AutoRegisterProperties;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.registercenter.api.RegisterCenter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

/**
 * @Author：ruayou
 * @Date：2024/2/21 16:42
 * @Filename：ClientRegisterManager
 */
@Slf4j
public abstract class AbstractRegisterManager {

    @Getter
    AutoRegisterProperties properties;

    private RegisterCenter registerCenter;

    protected AbstractRegisterManager(AutoRegisterProperties properties) {
        this.properties = properties;

        //初始化注册中心对象
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        //获取注册中心实现 如果没有就报错
        registerCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
        //注册中心初始化代码
        registerCenter.init(properties.getAddress(), properties.getEnv());
    }

    protected void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        //直接调用注册中心的api
        registerCenter.register(serviceDefinition, serviceInstance);
    }

}

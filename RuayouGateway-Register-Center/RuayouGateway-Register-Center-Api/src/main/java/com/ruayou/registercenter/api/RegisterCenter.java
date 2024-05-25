package com.ruayou.registercenter.api;


import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.GatewayException;

public interface RegisterCenter {

    /**
     *   初始化
     * @param registerAddress  注册中心地址
     * @param env  要注册到的环境
     */
    void init(String registerAddress, String env);

    /**
     * 注册
     * @param serviceDefinition 服务定义信息
     * @param serviceInstance 服务实例信息
     */
    void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) throws GatewayException;

    /**
     * 注销
     * @param serviceDefinition
     * @param serviceInstance
     */
    void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 订阅所有服务变更
     * @param registerCenterListener
     */
    void subscribeAllServices(RegisterCenterListener registerCenterListener);
}

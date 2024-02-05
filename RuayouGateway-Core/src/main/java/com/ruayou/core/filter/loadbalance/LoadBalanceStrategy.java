package com.ruayou.core.filter.loadbalance;

import com.ruayou.common.entity.ServiceInstance;

/**
 * @Author：ruayou
 * @Date：2024/2/5 23:17
 * @Filename：LoadBalance
 */
public interface LoadBalanceStrategy {


    /**
     * 通过服务ID拿到对应的服务实例
     * @param serviceId
     * @param gray
     * @return
     */
    ServiceInstance choose(String serviceId,boolean gray);
}
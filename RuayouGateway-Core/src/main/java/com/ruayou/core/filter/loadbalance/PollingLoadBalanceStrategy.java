package com.ruayou.core.filter.loadbalance;

import com.ruayou.core.manager.ServiceAndInstanceManager;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.InstanceException;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ruayou.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @Author：ruayou
 * @Date：2024/3/6 22:59
 * @Filename：PollingLoadBalanceStrategy
 * 轮询负载均衡策略
 */
@Log4j2
public class PollingLoadBalanceStrategy implements LoadBalanceStrategy{
    private final AtomicInteger flag = new AtomicInteger(1);

    private final String serviceId;
    private static ConcurrentHashMap<String, PollingLoadBalanceStrategy> map = new ConcurrentHashMap<>();

    public PollingLoadBalanceStrategy(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public ServiceInstance choose(String serviceId, String version, boolean gray) {
        Set<ServiceInstance> serviceInstanceSet =
                ServiceAndInstanceManager.getManager().getServiceInstanceByServiceId(serviceId,gray,version);
        if (serviceInstanceSet.isEmpty()) {
            log.warn("No instance available for:{}", serviceId);
            throw new InstanceException(SERVICE_INSTANCE_NOT_FOUND);
        }
        List<ServiceInstance> instances = new ArrayList<>(serviceInstanceSet);
        if (instances.isEmpty()) {
            log.warn("No instance available for service:{}", serviceId);
            throw new InstanceException(SERVICE_INSTANCE_NOT_FOUND);
        } else {
            int pos = Math.abs(this.flag.incrementAndGet());
            return instances.get(pos % instances.size());
        }
    }

    public static PollingLoadBalanceStrategy getInstance(String serviceId) {
        PollingLoadBalanceStrategy loadBalanceRule = map.get(serviceId);
        if (loadBalanceRule == null) {
            loadBalanceRule = new PollingLoadBalanceStrategy(serviceId);
            map.put(serviceId, loadBalanceRule);
        }
        return loadBalanceRule;
    }
}

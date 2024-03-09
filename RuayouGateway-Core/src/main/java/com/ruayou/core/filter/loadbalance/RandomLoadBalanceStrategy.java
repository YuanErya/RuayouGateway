package com.ruayou.core.filter.loadbalance;

import com.ruayou.core.manager.ServiceAndInstanceManager;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.InstanceException;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.ruayou.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;


/**
 * @Author：ruayou
 * @Date：2024/2/5 23:18
 * @Filename：RandomLoadBalanceStrategy
 * 随机负载均衡
 */
@Log4j2
public class RandomLoadBalanceStrategy implements LoadBalanceStrategy {

    //每一个服务走一个负载均衡实例
    private final String serviceId;
    private static ConcurrentHashMap<String, RandomLoadBalanceStrategy> map = new ConcurrentHashMap<>();

    public RandomLoadBalanceStrategy(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public ServiceInstance choose(String serviceId,String version ,boolean gray) {
        Set<ServiceInstance> serviceInstanceSet =
                ServiceAndInstanceManager.getManager().getServiceInstanceByServiceId(serviceId,gray,version);
        if (serviceInstanceSet.isEmpty()) {
            log.warn("No instance available for:{}", serviceId);
            throw new InstanceException(SERVICE_INSTANCE_NOT_FOUND);
        }
        List<ServiceInstance> instances = new ArrayList<>(serviceInstanceSet);
        int index = ThreadLocalRandom.current().nextInt(instances.size());//生成的随机整数将落在 0 到 instances.size() - 1（包含）之间。
        return instances.get(index);
    }

    public static RandomLoadBalanceStrategy getInstance(String serviceId) {
        RandomLoadBalanceStrategy loadBalanceRule = map.get(serviceId);
        if (loadBalanceRule == null) {
            loadBalanceRule = new RandomLoadBalanceStrategy(serviceId);
            map.put(serviceId, loadBalanceRule);
        }
        return loadBalanceRule;
    }
}

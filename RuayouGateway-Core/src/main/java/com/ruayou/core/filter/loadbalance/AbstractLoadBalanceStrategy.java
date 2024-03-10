package com.ruayou.core.filter.loadbalance;

import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.InstanceException;
import com.ruayou.core.manager.ServiceAndInstanceManager;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static com.ruayou.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @Author：ruayou
 * @Date：2024/3/10 22:16
 * @Filename：AbstractLoadBalanceStrategy
 */
@Log4j2
public abstract class AbstractLoadBalanceStrategy implements LoadBalanceStrategy{
    private static final ConcurrentHashMap<String, LoadBalanceStrategy> map = new ConcurrentHashMap<>();
    @Override
    public ServiceInstance choose(String serviceId,String version ,boolean gray) {
        Set<ServiceInstance> serviceInstanceSet =
                ServiceAndInstanceManager.getManager().getServiceInstanceByServiceId(serviceId,gray,version);
        if (serviceInstanceSet.isEmpty()) {
            log.warn("No instance available for:{}", serviceId);
            throw new InstanceException(SERVICE_INSTANCE_NOT_FOUND);
        }
        List<ServiceInstance> instances = new ArrayList<>(serviceInstanceSet);
        return doChoose(instances);
    }

    @Override
    public LoadBalanceStrategy getInstance(String serviceKey) {
        LoadBalanceStrategy iGatewayLoadBalanceRule = map.get(serviceKey);
        if (iGatewayLoadBalanceRule == null) {
            iGatewayLoadBalanceRule = getObject();
            map.put(serviceKey, iGatewayLoadBalanceRule);
        }
        return iGatewayLoadBalanceRule;
    }

    protected ServiceInstance doChoose(List<ServiceInstance> instanceList) {
        throw new UnsupportedOperationException();
    }

    protected LoadBalanceStrategy getObject() {
        throw new UnsupportedOperationException();
    }
}

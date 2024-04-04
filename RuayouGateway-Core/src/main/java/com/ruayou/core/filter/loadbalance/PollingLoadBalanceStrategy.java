package com.ruayou.core.filter.loadbalance;

import com.ruayou.common.constant.FilterConst;
import com.ruayou.common.entity.ServiceInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author：ruayou
 * @Date：2024/3/6 22:59
 * @Filename：PollingLoadBalanceStrategy
 * 轮询负载均衡策略
 */
@Slf4j
public class PollingLoadBalanceStrategy extends AbstractLoadBalanceStrategy{
    private final AtomicInteger flag = new AtomicInteger(0);
    @Override
    public boolean ifFit(String type) {
        return FilterConst.LOAD_BALANCE_STRATEGY_POLLING.equals(type);
    }
    protected ServiceInstance doChoose(List<ServiceInstance> instanceList) {
        int pos = Math.abs(this.flag.incrementAndGet());
        return instanceList.get(pos % instanceList.size());
    }
    protected LoadBalanceStrategy getObject() {
        return this;
    }
}

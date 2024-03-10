package com.ruayou.core.filter.loadbalance;

import com.ruayou.common.constant.FilterConst;
import com.ruayou.common.entity.ServiceInstance;
import lombok.extern.log4j.Log4j2;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @Author：ruayou
 * @Date：2024/2/5 23:18
 * @Filename：RandomLoadBalanceStrategy
 * 随机负载均衡
 */
@Log4j2
public class RandomLoadBalanceStrategy extends AbstractLoadBalanceStrategy{
    @Override
    public ServiceInstance doChoose(List<ServiceInstance> instanceList) {
        int index = ThreadLocalRandom.current().nextInt(instanceList.size());//生成的随机整数将落在 0 到 instances.size() - 1（包含）之间。
        return instanceList.get(index);
    }
    @Override
    public LoadBalanceStrategy getObject() {
        return this;
    }
    @Override
    public boolean ifFit(String type) {
        return FilterConst.LOAD_BALANCE_STRATEGY_RANDOM.equals(type);
    }
}

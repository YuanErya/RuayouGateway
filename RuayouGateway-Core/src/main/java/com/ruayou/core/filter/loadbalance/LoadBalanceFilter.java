package com.ruayou.core.filter.loadbalance;

import com.github.benmanes.caffeine.cache.Cache;
import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.enums.ResponseCode;
import com.ruayou.common.exception.GatewayException;
import com.ruayou.common.exception.InstanceException;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.request.GatewayRequest;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.filter.flowcontrol.FlowControlFilter;
import com.ruayou.core.manager.CacheManager;
import io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;
import java.util.ServiceLoader;

import static com.ruayou.common.constant.FilterConst.*;
import static com.ruayou.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;
/**
 * @Author：ruayou
 * @Date：2024/2/5 23:33
 * @Filename：LoadBalanceFilter
 */
@Log4j2
@GFilter(id = LOAD_BALANCE_FILTER_ID, name = LOAD_BALANCE_FILTER_NAME, order = LOAD_BALANCE_FILTER_ORDER)
public class LoadBalanceFilter implements Filter {
    private final static Cache<String, LoadBalanceStrategy> loadBaStrategyCache = CacheManager.createCache(CacheManager.FILTER_RULE_CACHE,"loadBaStrategyCache");
    /**
     * 注意ctx.getRequest()的构建时候。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        String serviceId = ctx.getServiceId();
        LoadBalanceStrategy loadBalanceStrategy =loadBaStrategyCache.get(serviceId,(k)->getLoadBalanceStrategy(ctx.getFilterRule().getLoadBalanceConfig().getStrategy(),k));
        ServiceInstance instance = loadBalanceStrategy.choose(ctx.getServiceId(), ctx.getFilterRule().getVersion(), ctx.isGray());
        log.debug("选择实例：{}", instance.toString());
        GatewayRequest request = ctx.getRequest();
        if (instance != null && request != null) {
            String host = instance.getIp() + ":" + instance.getPort();
            request.setModifyHost(host);
        } else {
            log.warn("No instance available for :{}", serviceId);
            throw new InstanceException(SERVICE_INSTANCE_NOT_FOUND);
        }
    }

    public LoadBalanceStrategy getLoadBalanceStrategy(String type,String serviceId) {
        ServiceLoader<LoadBalanceStrategy> serviceLoader = ServiceLoader.load(LoadBalanceStrategy.class);
        Iterator<LoadBalanceStrategy> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            LoadBalanceStrategy strategy = iterator.next();
            if (strategy.ifFit(type) ){
                return strategy.getInstance(serviceId+type);
            }
        }
        throw new GatewayException("负载均衡模式配置异常！",ResponseCode.INTERNAL_ERROR);
    }
}

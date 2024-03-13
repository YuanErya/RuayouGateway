package com.ruayou.core.filter.flowcontrol;

import com.github.benmanes.caffeine.cache.Cache;
import com.ruayou.core.filter.GatewayFilterChain;
import com.ruayou.core.filter.filter_rule.FilterRule;
import com.ruayou.common.constant.FilterConst;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.filter.flowcontrol.strategy.FlowControlStrategy;
import com.ruayou.core.manager.CacheManager;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.ServiceLoader;

import static com.ruayou.common.constant.FilterConst.*;

/**
 * @Author：ruayou
 * @Date：2024/3/4 17:06
 * @Filename：FlowControlFilter
 */
@GFilter(id = FLOW_CTL_FILTER_ID,
        name = FLOW_CTL_FILTER_NAME,
        order = FLOW_CTL_FILTER_ORDER)
public class FlowControlFilter implements Filter {
    private final static Cache<String, FlowControlWrapper> flowStrategyCache = CacheManager.createCache(CacheManager.FILTER_RULE_CACHE,"flowStrategyCache");
    @Override
    public void doFilter(GatewayContext ctx){
        List<FilterRule.FlowControlConfig> flowControlConfigs = ctx.getFilterRule().getFlowControlConfigs();
        FlowControlWrapper wrapper=flowStrategyCache.get(ctx.getServiceId(), (k)-> getFlowControlStrategy(flowControlConfigs,k));
        FlowControlStrategy strategy=wrapper.getStrategy();
        if (strategy!=null) {
            strategy.doFlowControl(ctx,wrapper.getConfig());
        }
    }

    private static FlowControlWrapper getFlowControlStrategy (List<FilterRule.FlowControlConfig> flowControlConfigs,String serviceId){
        FlowControlStrategy strategy = null;
        for (FilterRule.FlowControlConfig flowControlConfig : flowControlConfigs) {
            if (flowControlConfig == null || flowControlConfig.getFlowRule() == null || flowControlConfig.getFlowRule().isEmpty()) {
                continue;
            }
            if (flowControlConfig.getServiceIds().contains(serviceId)) {
                String type = flowControlConfig.getType();
                ServiceLoader<FlowControlStrategy> serviceLoader = ServiceLoader.load(FlowControlStrategy.class);
                for (FlowControlStrategy flowControlStrategy : serviceLoader) {
                    strategy = flowControlStrategy;
                    if (strategy.isFit(type)) {
                        return new FlowControlWrapper(flowControlConfig,strategy);
                    }
                }
            }
        }
        return new FlowControlWrapper(null,null);
    }

    @Data
    @AllArgsConstructor
    static class  FlowControlWrapper{
        FilterRule.FlowControlConfig config;
        FlowControlStrategy strategy;
    }
}

package com.ruayou.core.filter.flowcontrol;

import com.ruayou.core.filter.filter_rule.FilterRule;
import com.ruayou.common.constant.FilterConst;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.filter.flowcontrol.strategy.FlowControlStrategy;
import com.ruayou.core.filter.flowcontrol.strategy.FlowControllerByPathStrategy;
import com.ruayou.core.filter.flowcontrol.strategy.FlowControllerByServiceStrategy;

import java.util.Iterator;
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
    @Override
    public void doFilter(GatewayContext ctx){
        List<FilterRule.FlowControlConfig> flowControlConfigs = ctx.getFilterRule().getFlowControlConfigs();
        String serviceId = ctx.getServiceId();
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
                        strategy.doFlowControl(ctx, flowControlConfig);
                        break;
                    }
                    strategy=null;
                }
                if (strategy!=null) {
                    break;
                }
            }
        }
    }
}

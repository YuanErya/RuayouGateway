package com.ruayou.core.filter.flowcontrol;

import com.ruayou.common.config.FilterRule;
import com.ruayou.common.constant.FilterConst;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.filter.flowcontrol.strategy.FlowControlStrategy;
import com.ruayou.core.filter.flowcontrol.strategy.FlowControllerByPathStrategy;
import com.ruayou.core.filter.flowcontrol.strategy.FlowControllerByServiceStrategy;
import java.util.List;
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
                if (FilterConst.FLOW_CTL_TYPE_PATH.equals(type)) {
                    strategy = new FlowControllerByPathStrategy();
                } else if (FLOW_CTL_TYPE_SERVICE.equals(type)) {
                    strategy = new FlowControllerByServiceStrategy();
                }
                if (strategy!=null) {
                    strategy.doFlowControl(ctx,flowControlConfig);
                    break;
                }
            }
        }
    }


}

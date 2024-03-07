package com.ruayou.core.filter.flowcontrol.strategy;

import com.ruayou.common.config.FilterRule;
import com.ruayou.common.enums.ResponseCode;
import com.ruayou.common.exception.LimitedException;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.flowcontrol.limiter.LocalCountLimiter;
import static com.ruayou.common.constant.FilterConst.FLOW_CTL_FILTER_MODE_CLOUD;
import static com.ruayou.common.constant.FilterConst.FLOW_CTL_FILTER_MODE_LOCAL;

/**
 * @Author：ruayou
 * @Date：2024/3/7 20:28
 * @Filename：FlowControllerByServiceStrategy
 */
public class FlowControllerByServiceStrategy implements FlowControlStrategy{
    @Override
    public void doFlowControl(GatewayContext ctx, FilterRule.FlowControlConfig flowControlConfig) {
        String model = flowControlConfig.getModel();
        if (FLOW_CTL_FILTER_MODE_LOCAL.equals(model)) {
            LocalCountLimiter limiter = LocalCountLimiter.getServiceInstance(ctx.getServiceId(), flowControlConfig);
            if (limiter==null||!limiter.tryPass(1)) {
                throw  new LimitedException(ResponseCode.FLOW_CONTROL_ERROR);
            }
        } else if (FLOW_CTL_FILTER_MODE_CLOUD.equals(model)) {
            //云端流控。打算基于redis实现
        }
    }
}

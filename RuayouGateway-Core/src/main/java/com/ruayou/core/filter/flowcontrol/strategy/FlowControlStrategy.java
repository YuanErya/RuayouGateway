package com.ruayou.core.filter.flowcontrol.strategy;

import com.ruayou.core.filter.filter_rule.FilterRule;
import com.ruayou.core.context.GatewayContext;

/**
 * @Author：ruayou
 * @Date：2024/3/7 20:26
 * @Filename：FlowControlStrategy
 */
public interface FlowControlStrategy {

    void doFlowControl(GatewayContext ctx, FilterRule.FlowControlConfig flowControlConfig);

    boolean isFit(String type);

}

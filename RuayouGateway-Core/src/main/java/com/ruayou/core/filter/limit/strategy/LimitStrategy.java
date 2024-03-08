package com.ruayou.core.filter.limit.strategy;

import com.ruayou.common.config.FilterRule;
import com.ruayou.core.context.GatewayContext;

/**
 * @Author：ruayou
 * @Date：2024/3/7 23:32
 * @Filename：LimitStragery
 */
public interface LimitStrategy {

    boolean tryPass(String clientIp, FilterRule.LimitConfig config);
}

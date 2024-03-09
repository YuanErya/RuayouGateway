package com.ruayou.core.filter.limit.strategy;

import com.ruayou.core.filter.filter_rule.FilterRule;

/**
 * @Author：ruayou
 * @Date：2024/3/7 23:32
 * @Filename：LimitStragery
 */
public interface LimitStrategy {

    Boolean tryPass(String clientIp, FilterRule.LimitConfig config);
}

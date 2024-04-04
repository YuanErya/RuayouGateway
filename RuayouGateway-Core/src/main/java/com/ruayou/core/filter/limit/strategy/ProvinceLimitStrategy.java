package com.ruayou.core.filter.limit.strategy;

import com.ruayou.core.filter.filter_rule.FilterRule;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：ruayou
 * @Date：2024/3/8 23:31
 * @Filename：ProvinceLimitStrategy
 * 根据省份限流
 * 但是目前还没有想到比较好的方案区分省份
 */
@Slf4j
public class ProvinceLimitStrategy implements LimitStrategy{
    @Override
    public Boolean tryPass(String clientIp, FilterRule.LimitConfig config) {
        return false;
    }
}

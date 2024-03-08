package com.ruayou.core.filter.limit.strategy;

import com.ruayou.common.config.FilterRule;
import com.ruayou.core.context.GatewayContext;
import lombok.extern.log4j.Log4j2;

/**
 * @Author：ruayou
 * @Date：2024/3/8 23:31
 * @Filename：ProvinceLimitStrategy
 * 根据省份限流
 * 但是目前还没有想到比较好的方案区分省份
 */
@Log4j2
public class ProvinceLimitStrategy implements LimitStrategy{
    @Override
    public boolean tryPass(String clientIp, FilterRule.LimitConfig config) {
        return false;
    }
}

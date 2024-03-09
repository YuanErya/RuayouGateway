package com.ruayou.core.filter.limit;

import com.github.benmanes.caffeine.cache.Cache;
import com.ruayou.common.enums.ResponseCode;
import com.ruayou.common.exception.LimitedException;
import com.ruayou.core.filter.filter_rule.FilterRule;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.filter.limit.strategy.IPLimitStrategy;
import com.ruayou.core.filter.limit.strategy.LimitStrategy;
import com.ruayou.core.manager.CacheManager;

import static com.ruayou.common.constant.FilterConst.*;

/**
 * @Author：ruayou
 * @Date：2024/3/7 23:37
 * @Filename：LimitFilter
 */

@GFilter(id = LIMIT_FILTER_ID,
        name = LIMIT_FILTER_NAME,
        order = LIMIT_FILTER_ORDER)
public class LimitFilter implements Filter {
    Cache<String,Boolean> ipCache= CacheManager.createCache(CacheManager.FILTER_RULE_CACHE,"ipCache");
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        FilterRule.LimitConfig limitConfig = ctx.getFilterRule().getLimitConfig();
        if (limitConfig==null||limitConfig.getLimitRule()==null||limitConfig.getLimitRule().isEmpty()) {
            return;
        }
        LimitStrategy strategy=new IPLimitStrategy();
        if (!ipCache.get(ctx.getRequest().getClientIp(), k->strategy.tryPass(k,limitConfig))) {
            throw new LimitedException(ResponseCode.BLACKLIST);
        }
    }
}

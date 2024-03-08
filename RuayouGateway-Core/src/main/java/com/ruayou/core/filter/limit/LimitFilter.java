package com.ruayou.core.filter.limit;

import com.ruayou.common.config.FilterRule;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.filter.limit.strategy.IPLimitStrategy;
import com.ruayou.core.filter.limit.strategy.LimitStrategy;

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

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        FilterRule.LimitConfig limitConfig = ctx.getFilterRule().getLimitConfig();
        if (limitConfig==null||limitConfig.getLimitRule()==null||limitConfig.getLimitRule().isEmpty()) {
            return;
        }
        LimitStrategy strategy=new IPLimitStrategy();
        strategy.tryPass(ctx.getRequest().getClientIp(),limitConfig);
    }
}

package com.ruayou.core.filter.router;

import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import lombok.extern.log4j.Log4j2;

import static com.ruayou.common.constant.FilterConst.*;

/**
 * @Author：ruayou
 * @Date：2024/1/29 18:15
 * @Filename：RouterFilter
 */
@Log4j2
@GFilter(id = ROUTER_FILTER_ID, name = ROUTER_FILTER_NAME, order = ROUTER_FILTER_ORDER)
public class RouterFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {

    }

}

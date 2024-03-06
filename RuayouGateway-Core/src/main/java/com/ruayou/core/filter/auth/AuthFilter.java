package com.ruayou.core.filter.auth;

import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;

import static com.ruayou.common.constant.FilterConst.AUTH_FILTER_ORDER;
import static com.ruayou.common.constant.FilterConst.AUTH_FILTER_ID;
import static com.ruayou.common.constant.FilterConst.AUTH_FILTER_NAME;

/**
 * @Author：ruayou
 * @Date：2024/3/6 22:55
 * @Filename：AuthFilter
 */
@GFilter(id= AUTH_FILTER_ID,
        name = AUTH_FILTER_NAME,
        order =AUTH_FILTER_ORDER )
public class AuthFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {

    }
}

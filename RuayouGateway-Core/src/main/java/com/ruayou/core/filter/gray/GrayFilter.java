package com.ruayou.core.filter.gray;

import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import lombok.extern.log4j.Log4j2;

import static com.ruayou.common.constant.FilterConst.*;

/**
 * @Author：ruayou
 * @Date：2024/3/2 23:53
 * @Filename：GrayFilter
 * 灰度发布过滤器
 */
@Log4j2
@GFilter(id = GRAY_FILTER_ID,
        name = GRAY_FILTER_NAME,
        order = GRAY_FILTER_ORDER)
public class GrayFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {

    }
}

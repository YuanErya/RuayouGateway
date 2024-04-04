package com.ruayou.core.filter.mock;

import com.ruayou.core.filter.filter_rule.FilterRule;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.request.GatewayRequest;
import com.ruayou.core.context.response.GatewayResponse;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.helper.ResponseHelper;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.ruayou.common.constant.CommonConst.COLON_SEPARATOR;
import static com.ruayou.common.constant.FilterConst.*;

/**
 * @Author：ruayou
 * @Date：2024/3/4 16:48
 * @Filename：MockFilter
 */
@Slf4j
@GFilter(id=MOCK_FILTER_ID,
        name = MOCK_FILTER_NAME,
        order = MOCK_FILTER_ORDER)
public class MockFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        FilterRule.MockConfig mockConfig=ctx.getFilterRule().getMockConfig();
        if (mockConfig==null||mockConfig.getMockMap()==null||mockConfig.getMockMap().isEmpty()) {
            return ;
        }
        Map<String,String> mockMap=mockConfig.getMockMap();
        GatewayRequest request=ctx.getRequest();
        String value = mockMap.get(request.getMethod().name() + COLON_SEPARATOR + request.getPath());
        if (value!=null) {
            ctx.setResponse(GatewayResponse.buildGatewayResponse(value));
            ctx.written();
            ResponseHelper.writeResponse(ctx);
            log.debug("mock {}:{} response:{}",request.getMethod(),request.getPath(),value);
            ctx.terminated();//标记结束过滤器链条的执行
        }
    }
}

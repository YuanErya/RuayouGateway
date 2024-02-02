package com.ruayou.core.filter;

import com.ruayou.core.context.GatewayContext;

/**
 * @Author：ruayou
 * @Date：2024/2/1 23:45
 * @Filename：FilterChainFactory
 */
public interface FilterChainFactory {
    /**
     * 构建过滤器链条
     * @param ctx
     * @return
     * @throws Exception
     */
    GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception;

    /**
     * 通过过滤器ID获取过滤器
     * @param filterId
     * @return
     * @param <T>
     * @throws Exception
     */
    <T> T getFilterInfo(String filterId) throws Exception;
}

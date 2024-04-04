package com.ruayou.core.filter;

import com.ruayou.core.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：ruayou
 * @Date：2024/2/1 23:46
 * @Filename：GatewayFilterChain
 */
@Slf4j
public class GatewayFilterChain {
    private List<Filter> filters = new ArrayList<>();


    public GatewayFilterChain addFilter(Filter filter){
        filters.add(filter);
        return this;
    }
    public GatewayFilterChain addFilterList(List<Filter> filter){
        filters.addAll(filter);
        return this;
    }


    /**
     * 执行过滤器处理流程
     * @param ctx
     * @return
     * @throws Exception
     */
    public GatewayContext doFilters(GatewayContext ctx) throws Exception {
        if(filters.isEmpty()){
            return ctx;
        }
        try {
            for(Filter fl: filters){
                fl.doFilter(ctx);
                if (ctx.isTerminated()){
                    break;
                }
            }
        }catch (Exception e){
            throw e;
        }
        return ctx;
    }
}

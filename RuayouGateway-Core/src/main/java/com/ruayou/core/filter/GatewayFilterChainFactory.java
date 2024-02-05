package com.ruayou.core.filter;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ruayou.common.config.FilterRule;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.filter.router.RouterFilter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author：ruayou
 * @Date：2024/2/2 22:12
 * @Filename：GatewayFilterChainFactory
 */
@Log4j2
public class GatewayFilterChainFactory implements FilterChainFactory{

    private static final GatewayFilterChainFactory INSTANCE = new GatewayFilterChainFactory();

    public static GatewayFilterChainFactory getFactory(){
        return INSTANCE;
    }


    private Cache<String, GatewayFilterChain> chainCache = Caffeine.newBuilder().recordStats().expireAfterWrite(10,
            TimeUnit.MINUTES).build();


    private Map<String, Filter> processorFilterIdMap = new ConcurrentHashMap<>();

    private GatewayFilterChainFactory(){
        //加载所有过滤器
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
        serviceLoader.stream().forEach(filterProvider -> {
            Filter filter = filterProvider.get();
            GFilter annotation = filter.getClass().getAnnotation(GFilter.class);
            log.info("load filter success:{},{},{},{}", filter.getClass(), annotation.id(), annotation.name(),
                    annotation.order());
            //添加到过滤集合
            String filterId = annotation.id();
            if (StringUtils.isEmpty(filterId)) {
                filterId = filter.getClass().getName();
            }
            processorFilterIdMap.put(filterId, filter);
        });
    }

    //对规则建立缓存，过滤器链条的复用。
    @Override
    public GatewayFilterChain buildFilterChain(GatewayContext ctx){
        return chainCache.get(ctx.getFilterRule().getRuleId(),k->doBuildFilterChain(ctx.getFilterRule()));
    }

    @Override
    public Filter getFilterInfo(String filterId) {
        return processorFilterIdMap.get(filterId);
    }


    private GatewayFilterChain doBuildFilterChain(FilterRule rule){
        GatewayFilterChain chain = new GatewayFilterChain();
        List<Filter> filters = new ArrayList<>();
        //获取过滤器配置规则  是我们再配置中心进行配置的
        //这是由于我们的过滤器链是由我们的规则定义的
        if (rule != null) {
            //获取所有的过滤器
            List<String> filterIds = rule.getFilters();
            for (String filterId : filterIds) {
                if (filterId == null) {
                    continue;
                }
                if (StringUtils.isNotEmpty(filterId)) {
                    Filter filter = getFilterInfo(filterId);
                    if (filter != null) filters.add(filter);
                }
            }
        }
        //添加路由过滤器-因为我们的网关最后要执行的就是路由转发
        filters.add(new RouterFilter());
        //排序
        filters.sort(Comparator.comparingInt(Filter::getOrder));
        //添加到链表中
        chain.addFilterList(filters);
        return chain;
    }
}

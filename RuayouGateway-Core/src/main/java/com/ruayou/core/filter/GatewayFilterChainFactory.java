package com.ruayou.core.filter;


import com.ruayou.core.context.GatewayContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

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

//    private Cache<String, GatewayFilterChain> chainCache = Caffeine.newBuilder().recordStats().expireAfterWrite(10,
//            TimeUnit.MINUTES).build();


    private Map<String, Filter> processorFilterIdMap = new ConcurrentHashMap<>();

    public GatewayFilterChainFactory(){
        //加载所有过滤器
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
        serviceLoader.stream().forEach(filterProvider -> {
            Filter filter = filterProvider.get();
            GFilter annotation = filter.getClass().getAnnotation(GFilter.class);
            log.info("load filter success:{},{},{},{}", filter.getClass(), annotation.id(), annotation.name(),
                    annotation.order());
            if (annotation != null) {
                //添加到过滤集合
                String filterId = annotation.id();
                if (StringUtils.isEmpty(filterId)) {
                    filterId = filter.getClass().getName();
                }
                processorFilterIdMap.put(filterId, filter);
            }
        });
    }
    @Override
    public GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception {
        return null;
    }

    @Override
    public Filter getFilterInfo(String filterId) throws Exception {
        return processorFilterIdMap.get(filterId);
    }
}

package com.ruayou.core.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author：ruayou
 * @Date：2024/3/9 22:13
 * @Filename：CacheManager
 */
public class CacheManager {

    public static final String FILTER_RULE_CACHE="filter-cache";
    public static final String SERVICE_CACHE="service-cache";

    public static final Caffeine<Object, Object> caffeine=Caffeine.newBuilder().recordStats().expireAfterWrite(20, TimeUnit.MINUTES);
    public static Map<String,Map<String,Cache>> cacheMap=new HashMap<>();
    static {
        cacheMap.put(FILTER_RULE_CACHE,new HashMap<>());
        cacheMap.put(SERVICE_CACHE,new HashMap<>());
    }

    public static <T,U> Cache<T,U> createCache(String group,String id){
        Cache<T, U> cache = caffeine.build();
        cacheMap.get(group).put(id,cache);
        return cache;
    }

    /**
     * 清空分组所有缓存记录
     */
    public static void cleanAllCache(String group){
        cacheMap.get(group).forEach((k,v)->{
            v.invalidateAll();
        });
    }
}

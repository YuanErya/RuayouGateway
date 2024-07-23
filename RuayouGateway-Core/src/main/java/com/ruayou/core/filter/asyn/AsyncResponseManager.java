package com.ruayou.core.filter.asyn;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ruayou.core.context.response.GatewayResponse;

import java.util.concurrent.TimeUnit;

/**
 * @Author：ruayou
 * @Date：2024/7/13 18:10
 * @Filename：AsyncResponseManager
 */

public class AsyncResponseManager {
    private static final Caffeine<Object, Object> caffeine = Caffeine.newBuilder().recordStats().expireAfterWrite(30, TimeUnit.MINUTES);
    private static final Cache<String, GatewayResponse> asyncCache = caffeine.build();

    /**
     * 添加异步响应
     * @param key
     * @param response
     */
    public static void putAsyncResponse(final String key, final GatewayResponse response) {
        asyncCache.put(key, response);
    }

    /**
     * 获取异步响应
     * @param key
     * @return
     */
    public static GatewayResponse getAsyncResponse(final String key) {
        GatewayResponse response = asyncCache.getIfPresent(key);
        asyncCache.invalidate(key);
        return response;
    }
}

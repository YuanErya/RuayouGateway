package com.ruayou.core.filter.flowcontrol.limiter;

import com.google.common.util.concurrent.RateLimiter;
import com.ruayou.core.filter.filter_rule.FilterRule;

import java.util.concurrent.ConcurrentHashMap;

import static com.ruayou.common.constant.CommonConst.COLON_SEPARATOR;

/**
 * @Author：ruayou
 * @Date：2024/3/7 20:43
 * @Filename：LocalCountLimter
 */
public class LocalCountLimiter implements Limiter {
    private RateLimiter rateLimiter;

    private double maxPermits;

    public static final ConcurrentHashMap<String, LocalCountLimiter> pathLimiterMap = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, LocalCountLimiter> serviceLimiterMap = new ConcurrentHashMap<>();

    public LocalCountLimiter(double maxPermits) {
        this.maxPermits = maxPermits;
        rateLimiter = RateLimiter.create(maxPermits);
    }

    public static LocalCountLimiter getServiceInstance(String serviceId, FilterRule.FlowControlConfig flowControlConfig) {
        String key = serviceId + flowControlConfig.getType() + flowControlConfig.getId();
        return getLocalCountLimiter(serviceId, flowControlConfig, key, serviceLimiterMap);
    }

    public static LocalCountLimiter getPathInstance(String serviceId, String path, FilterRule.FlowControlConfig flowControlConfig) {
        String key = serviceId + flowControlConfig.getType() + flowControlConfig.getId() + COLON_SEPARATOR + path;
        return getLocalCountLimiter(path, flowControlConfig, key, pathLimiterMap);
    }

    private static LocalCountLimiter getLocalCountLimiter(String keyword, FilterRule.FlowControlConfig flowControlConfig, String key, ConcurrentHashMap<String, LocalCountLimiter> serviceLimiterMap) {
        LocalCountLimiter countLimiter = serviceLimiterMap.get(key);
        if (countLimiter == null) {
            Integer permit = flowControlConfig.getFlowRule().get(keyword);
            if (permit == null) {
                return null;
            } else {
                countLimiter = new LocalCountLimiter(permit);
                serviceLimiterMap.putIfAbsent(key, countLimiter);
            }
        }
        return countLimiter;
    }

    @Override
    public boolean tryPass(int permits) {
        return rateLimiter.tryAcquire(permits);
    }

    public static void cleanCache() {
        pathLimiterMap.clear();
        serviceLimiterMap.clear();
    }
}

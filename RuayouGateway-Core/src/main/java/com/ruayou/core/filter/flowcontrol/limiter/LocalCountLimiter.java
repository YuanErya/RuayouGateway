package com.ruayou.core.filter.flowcontrol.limiter;

import com.google.common.util.concurrent.RateLimiter;
import com.ruayou.common.config.FilterRule;

import java.util.concurrent.ConcurrentHashMap;

import static com.ruayou.common.constant.CommonConst.COLON_SEPARATOR;

/**
 * @Author：ruayou
 * @Date：2024/3/7 20:43
 * @Filename：LocalCountLimter
 */
public class LocalCountLimiter {
    private RateLimiter rateLimiter;

    private double maxPermits;

    public static final ConcurrentHashMap<String,LocalCountLimiter>  pathLimiterMap=new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String,LocalCountLimiter>  serviceLimiterMap=new ConcurrentHashMap<>();

    public LocalCountLimiter(double maxPermits) {
        this.maxPermits = maxPermits;
        rateLimiter = RateLimiter.create(maxPermits);
    }

    public static LocalCountLimiter getServiceInstance(String serviceId, FilterRule.FlowControlConfig flowControlConfig) {
        String key = serviceId+flowControlConfig.getType()+flowControlConfig.getId();
        LocalCountLimiter countLimiter = serviceLimiterMap.get(key);
        if (countLimiter==null) {
            Integer permit = flowControlConfig.getFlowRule().get(serviceId);
            if (permit==null) {
                return null;
            }else{
                countLimiter = new LocalCountLimiter(permit);
                serviceLimiterMap.putIfAbsent(key,countLimiter);
            }
        }
        return countLimiter;
    }

    public boolean tryPass(int permits) {
        return rateLimiter.tryAcquire(permits);
    }

    public static LocalCountLimiter getPathInstance(String serviceId, String path, FilterRule.FlowControlConfig flowControlConfig){
        String key = serviceId+flowControlConfig.getType()+flowControlConfig.getId()+COLON_SEPARATOR+path;
        LocalCountLimiter countLimiter = pathLimiterMap.get(key);
        if (countLimiter==null) {
            Integer permit = flowControlConfig.getFlowRule().get(path);
            if (permit==null) {
                return null;
            }else{
                countLimiter = new LocalCountLimiter(permit);
                pathLimiterMap.putIfAbsent(key,countLimiter);
            }
        }
        return countLimiter;
    }
}

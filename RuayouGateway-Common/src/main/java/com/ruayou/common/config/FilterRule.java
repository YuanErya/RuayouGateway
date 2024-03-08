package com.ruayou.common.config;

import lombok.Data;

import java.util.*;

import static com.ruayou.common.constant.FilterConst.LIMIT_FILTER_TYPE_IP;
import static com.ruayou.common.constant.FilterConst.LOAD_BALANCE_STRATEGY_POLLING;

/**
 * @Author：ruayou
 * @Date：2024/2/4 17:53
 * @Filename：FilterRule
 */
@Data
public class FilterRule {
    /**
     * 规则ID，全局唯一
     */
    private String ruleId;

    /**
     * 协议
     */
    private String protocol="http";

    /**
     * 版本
     */
    private String version;
    /**
     * 生效的服务
     */
    private Set<String> serviceIds;
    /**
     * 路径匹配模式
     */
    private Map<String,String> patterns =new HashMap<>();
    /**
     * 优先级
     */
    private Integer order;
    /**
     * 存放需要用到的过滤器的id
     */
    private Set<String> filters;

    /**
     * 重试次数配置
     */
    private RetryConfig retryConfig =new RetryConfig();
    /**
     * Mock配置
     */
    private MockConfig mockConfig;
    /**
     * 负载均衡配置
     */
    private LoadBalanceConfig loadBalanceConfig=new LoadBalanceConfig();
    /**
     * 黑名单配置
     */
    private LimitConfig limitConfig;
    /**
     * 流量控制
     */
    private List<FlowControlConfig> flowControlConfigs = new ArrayList<>();


    @Data
    public static class FlowControlConfig {
        private String id;
        /**
         * 限流类型 path，IP，服务
         */
        private String type;
        /**
         * 适配的流量控制的serviceIds
         */
        private Set<String> serviceIds;
        /**
         * 限流模式-分布式（cloud），单机（local）
         */
        private String model;
        /**
         * 优先级，一般来说细粒度的应当优先级高
         */
        private Integer order;
        /**
         * map存储限流规则
         */
        private Map<String,Integer> flowRule;
    }
    @Data
    public static class RetryConfig {
        private int retryCount =2;
    }
    @Data
    public static class MockConfig {
        /**
         * key:请求方式：路径
         * value：结果
         */
        Map<String,String> MockMap=new HashMap<>();
    }

    @Data
    public static class LimitConfig {
        /**
         * 默认根据ip段
         */
        private String type=LIMIT_FILTER_TYPE_IP;
        /**
         * 限制规则
         */
        private Set<String> limitRule;
    }

    @Data
    public static class LoadBalanceConfig {
        //默认负载均衡是轮询
        private String strategy =LOAD_BALANCE_STRATEGY_POLLING;
    }
}

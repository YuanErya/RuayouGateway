package com.ruayou.common.config;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<String> serviceIds;
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
    private List<String> filters;

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
//    private HystrixConfig hystrixConfig;


//    @Data
//    public static class FlowControlConfig {
//        /**
//         * 限流类型-可能是path，也可能是IP或者服务
//         */
//        private String type;
//        /**
//         * 限流对象的值
//         */
//        private String value;
//        /**
//         * 限流模式-单机还有分布式
//         */
//        private String model;
//        /**
//         * 限流规则,是一个JSON
//         */
//        private String config;
//    }
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
    public static class LoadBalanceConfig {
        //默认负载均衡是轮询
        private String strategy =LOAD_BALANCE_STRATEGY_POLLING;
    }
//    @Data
//    public static class HystrixConfig {
//        /**
//         * 熔断降级陆军
//         */
//        private String path;
//        /**
//         * 超时时间
//         */
//        private int timeoutInMilliseconds;
//        /**
//         * 核心线程数量
//         */
//        private int threadCoreSize;
//        /**
//         * 熔断降级响应
//         */
//        private String fallbackResponse;
//    }
}

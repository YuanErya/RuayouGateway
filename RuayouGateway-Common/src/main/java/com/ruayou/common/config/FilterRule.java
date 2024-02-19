package com.ruayou.common.config;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String protocol;

    /**
     * 后端服务ID
     */
    private String serviceId;

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

//    private FlowControlConfig flowControlConfig;
    private RetryConfig retryConfig;
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
        private int retryCount;
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

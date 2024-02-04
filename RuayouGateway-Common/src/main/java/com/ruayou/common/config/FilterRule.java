package com.ruayou.common.config;

import java.util.List;

/**
 * @Author：ruayou
 * @Date：2024/2/4 17:53
 * @Filename：FilterRule
 */
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
    private List<String> patterns;

    /**
     * 优先级
     */
    private Integer order;



}

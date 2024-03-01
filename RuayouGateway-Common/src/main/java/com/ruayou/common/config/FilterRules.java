package com.ruayou.common.config;

import com.ruayou.common.constant.FilterConst;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：ruayou
 * @Date：2024/2/28 21:16
 * @Filename：FilterRules
 */
public class FilterRules {
    public static final String dataId = "filter-rules";
    private static FilterRules GlobalRules = new FilterRules();

    public static FilterRules getGlobalRules() {
        return GlobalRules;
    }

    public static void updateRules(FilterRules rules) {
        GlobalRules = rules;
        ServiceAndInstanceManager.getManager().putAllFilterRules(rules);
    }

    @Getter
    @Setter
    Map<String,FilterRule> rules = new HashMap<>();

    private static FilterRule defaultFilterRule;

    /**
     * 添加默认的路由配置规则
     * @param map
     * @return
     */
    public static FilterRule getDefaultFilterRule(Map<String, String> map) {
        if (defaultFilterRule == null) {
            FilterRule rule = new FilterRule();
            rule.setFilters(List.of(FilterConst.LOAD_BALANCE_FILTER_ID));
            rule.setRuleId("default");
            rule.setOrder(Integer.MAX_VALUE);
            rule.setRetryConfig(new FilterRule.RetryConfig());
            rule.setPatterns(map);
            defaultFilterRule = rule;
        }else {
            defaultFilterRule.getPatterns().putAll(map);
        }
        return defaultFilterRule;
    }

    public void addRule(FilterRule rule) {
        rules.put(rule.getRuleId(),rule);
    }
}

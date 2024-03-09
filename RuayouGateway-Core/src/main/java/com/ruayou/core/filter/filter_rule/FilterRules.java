package com.ruayou.core.filter.filter_rule;

import com.ruayou.common.constant.FilterConst;
import com.ruayou.core.manager.ServiceAndInstanceManager;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruayou.common.constant.ServiceConst.*;

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
    Map<String, FilterRule> rules = new HashMap<>();

    private static FilterRule defaultFilterRule;

    /**
     * 添加默认的路由配置规则
     *
     * @param patternsMap
     * @returnd
     */
    public static void updateDefaultFilterRule(Map<String, String> patternsMap) {
        creatDefaultFilterRule();
        defaultFilterRule.getPatterns().putAll(patternsMap);
    }

    public static FilterRule getDefaultFilterRule() {
        creatDefaultFilterRule();
        return defaultFilterRule;
    }
    public void addRule(FilterRule rule) {
        rules.put(rule.getRuleId(), rule);
    }

    private static void creatDefaultFilterRule() {
        if (defaultFilterRule == null) {
            FilterRule rule = new FilterRule();
            rule.setFilters(List.of(FilterConst.LOAD_BALANCE_FILTER_ID));
            rule.setRuleId(DEFAULT_ID);
            rule.setVersion(DEFAULT_VERSION);
            rule.setOrder(Integer.MAX_VALUE);
            rule.setProtocol(DEFAULT_PROTOCOL);
            rule.setRetryConfig(new FilterRule.RetryConfig());
            rule.setPatterns(new HashMap<>());
            defaultFilterRule = rule;
        }
    }
}

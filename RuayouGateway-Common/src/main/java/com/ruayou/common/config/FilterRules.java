package com.ruayou.common.config;

import com.ruayou.common.constant.FilterConst;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：ruayou
 * @Date：2024/2/28 21:16
 * @Filename：FilterRules
 */
public class FilterRules {
    public static final String dataId="filter-rules";

    private static FilterRules GlobalRules =new FilterRules();

    public static FilterRules getGlobalRules() {
        return GlobalRules;
    }
    public static void updateRules(FilterRules rules){
        GlobalRules=rules;
        ServiceAndInstanceManager.getManager().putAllFilterRules(rules);
    }
    @Getter@Setter
    List<FilterRule> rules=new ArrayList<>();
    public static final FilterRule defaultRule=new FilterRule();


    static {
        defaultRule.setFilters(List.of(FilterConst.LOAD_BALANCE_FILTER_ID));
        defaultRule.setRuleId("default");
        defaultRule.setOrder(Integer.MAX_VALUE);
        defaultRule.setRetryConfig(new FilterRule.RetryConfig());
    }
    public static FilterRule getDefaultFilterRule(){
        return defaultRule;
    }

    public void addRule(FilterRule rule){
        rules.add(rule);
    }
}

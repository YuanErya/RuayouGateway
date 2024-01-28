package com.ruayou.common.api_interface.config_center;


import com.ruayou.common.entity.Rule;

import java.util.List;

public interface RulesChangeListener {

    /**
     * 规则变更时调用此方法 对规则进行更新
     * @param rules 新规则
     */
    void onRulesChange(List<Rule> rules);
}

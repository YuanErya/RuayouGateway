package com.ruayou.common.api_interface.config_center;

public interface ConfigChangeListener {

    /**
     * 配置变更时调用此方法 对配置进行更新
     */
    void onConfigChange(String configInfo);
}

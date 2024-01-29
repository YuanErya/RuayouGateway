package com.ruayou.common.api_interface.config_center;

import com.ruayou.common.api_interface.Config;

public interface ConfigChangeListener {

    /**
     * 配置变更时调用此方法 对配置进行更新
     */
    void onConfigChange(Config config);
}

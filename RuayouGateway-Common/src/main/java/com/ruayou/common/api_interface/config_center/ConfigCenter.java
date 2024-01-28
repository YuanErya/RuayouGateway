package com.ruayou.common.api_interface.config_center;


public interface ConfigCenter {

    /**
     * 初始化配置中心配置
     * @param serverAddr  配置中心地址
     * @param env 环境
     */
    void init(String serverAddr, String env);

    /**
     * 订阅配置中心配置变更
     * @param listener  配置变更监听器
     */
    void subscribeRulesChange(RulesChangeListener listener);
}

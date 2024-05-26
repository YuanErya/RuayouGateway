package com.ruayou.common.config;

import com.ruayou.common.api_interface.Config;
import lombok.Data;

/**
 * @Author：ruayou
 * @Date：2024/1/30 21:59
 * @Filename：NacosConfig
 */
@Data
public class RegisterAndConfigCenterConfig implements Config {
    public static final String REGISTER_ZOOKEEPER_SERVER = "com.ruayou.register_center.zookeeperimpl.ZookeeperRegisterCenter";
    public static final String REGISTER_NACOS_SERVER = "com.ruayou.register_center.nacosimpl.NacosRegisterCenter";
    public static final String CONFIG_ZOOKEEPER_SERVER = "com.ruayou.config_center.zookeeperimpl.ZookeeperConfigCenter";
    public static final String CONFIG_NACOS_SERVER = "com.ruayou.config_center.nacosimpl.NacosConfigCenter";

    private String applicationName = "ruayou-gateway";
    private String env = "dev";

    private String registerServer=REGISTER_ZOOKEEPER_SERVER;
    private String registryAddress = "127.0.0.1:2181";

    private String configServer=CONFIG_ZOOKEEPER_SERVER;
    private String configAddress = "127.0.0.1:2181";


}

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
    public static final String ZOOKEEPER_SERVER = "com.ruayou.register_center.zookeeperimpl.ZookeeperRegisterCenter";
    public static final String NACOS_SERVER = "com.ruayou.register_center.nacosimpl.NacosRegisterCenter";
    private String server=ZOOKEEPER_SERVER;
    private String applicationName = "ruayou-gateway";
    private String registryAddress = "127.0.0.1:2181";
    private String env = "dev";

}

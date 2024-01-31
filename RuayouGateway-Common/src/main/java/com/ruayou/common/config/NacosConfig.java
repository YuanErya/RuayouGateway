package com.ruayou.common.config;

import com.ruayou.common.api_interface.Config;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author：ruayou
 * @Date：2024/1/30 21:59
 * @Filename：NacosConfig
 */
@Data
public class NacosConfig implements Config {
    private String applicationName = "ruayou-gateway";
    private String registryAddress = "127.0.0.1:8848";
    private String env = "DEFAULT_GROUP";
}

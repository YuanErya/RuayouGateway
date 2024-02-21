package com.ruayou.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author：ruayou
 * @Date：2024/2/21 16:36
 * @Filename：AutoRegisterConfig
 */
@Data
@ConfigurationProperties(prefix = "ruayou.register")
public class AutoRegisterProperties {
    /**
     * 注册中心地址
     */
    private String address;
    /**
     * 环境
     */
    private String env = "dev";
    /**
     * 是否灰度发布
     */
    private boolean gray;
}

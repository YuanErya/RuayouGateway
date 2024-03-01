package com.ruayou.common.config;

import com.ruayou.common.api_interface.Config;
import lombok.Data;

/**
 * @Author：ruayou
 * @Date：2024/1/26 20:18
 * @Filename：Config
 */
@Data
public class NettyServerConfig implements Config {
    private int eventLoopGroupWorkerNum = 8;
    private int port = 8999;
    private int maxContentLength = 64 * 1024 * 1024;
}

package com.ruayou.core.netty;

import lombok.Data;

/**
 * @Author：ruayou
 * @Date：2024/1/26 20:18
 * @Filename：Config
 */
@Data
public class NettyServerConfig {
    private int eventLoopGroupWorkerNum = 1;
    private int port = 8999;
    private int maxContentLength = 64 * 1024 * 1024;
}

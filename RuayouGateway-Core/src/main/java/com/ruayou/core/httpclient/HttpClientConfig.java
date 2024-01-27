package com.ruayou.core.httpclient;

import lombok.Data;

/**
 * @Author：ruayou
 * @Date：2024/1/27 20:40
 * @Filename：HttpClientConfig
 */
@Data
public class HttpClientConfig {
    //	Http Async 参数选项：
    //	连接超时时间
    private int httpConnectTimeout = 30 * 1000;
    //	请求超时时间
    private int httpRequestTimeout = 30 * 1000;

    //	客户端请求重试次数
    private int httpMaxRequestRetry = 2;
    //	客户端请求最大连接数
    private int httpMaxConnections = 10000;
    //	客户端每个地址支持的最大连接数
    private int httpConnectionsPerHost = 8000;
    //	客户端空闲连接超时时间, 默认60秒
    private int httpPooledConnectionIdleTimeout = 60 * 1000;
}

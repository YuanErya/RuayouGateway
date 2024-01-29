package com.ruayou.core;

import com.ruayou.core.httpclient.HttpClientConfig;
import com.ruayou.core.netty.NettyServerConfig;
import lombok.extern.log4j.Log4j2;

/**
 * @Author：ruayou
 * @Date：2024/1/2 18:34
 * @Filename：Application
 * 程序入口
 */
public class Application {
    public static void main(String[] args) {
        ServerContainer container = new ServerContainer(new NettyServerConfig(), new HttpClientConfig());
        container.start();
    }
}

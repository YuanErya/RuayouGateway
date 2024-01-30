package com.ruayou.common.config;

import com.ruayou.common.api_interface.Config;
import lombok.Data;

/**
 * @Author：ruayou
 * @Date：2024/1/30 21:54
 * @Filename：GloablConfig
 */
@Data
public class GlobalConfig implements Config {
    public static String dataId="GlobalConfig";
    private  NettyServerConfig nettyServerConfig=new NettyServerConfig();
    private  HttpClientConfig httpClientConfig=new HttpClientConfig();
    private  NacosConfig nacosConfig=new NacosConfig();
}

package com.ruayou.config_center.nacosimpl;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.ruayou.common.api_interface.config_center.ConfigCenter;
import com.ruayou.common.api_interface.config_center.ConfigChangeListener;
import lombok.extern.log4j.Log4j2;


import java.util.concurrent.Executor;

/**
 * @Author：ruayou
 * @Date：2024/1/29 22:37
 * @Filename：NacosConfigCenter
 */
@Log4j2
public class NacosConfigCenter implements ConfigCenter {
    private  static NacosConfigCenter nacosConfigCenter;
    /**
     * 服务端地址
     */
    private String serverAddr;

    /**
     * 环境
     */
    private String env;

    private ConfigService configService;

    @Override
    public void init(String serverAddr, String env) {
        if(nacosConfigCenter!=null)return;
        this.serverAddr = serverAddr;
        this.env = env;
        try {
            this.configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        nacosConfigCenter=this;
    }

    @Override
    public void subscribeConfigChange(String dataId, ConfigChangeListener listener) {
        try {
            String configJson = configService.getConfig(dataId, env, 5000);
            log.info("config from nacos: {}", configJson);
            //监听变化
            configService.addListener(dataId, env, new Listener() {
                //是否使用额外线程执行
                @Override
                public Executor getExecutor() {
                    return null;
                }
                //这里的用法我在那片线程池动态调参的时候写到过,有兴趣可以查看博客
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("config from nacos: {}", configInfo);
                }
            });
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}

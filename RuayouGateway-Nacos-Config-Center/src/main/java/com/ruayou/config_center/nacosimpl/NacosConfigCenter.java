package com.ruayou.config_center.nacosimpl;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.ruayou.common.api_interface.config_center.ConfigCenter;
import com.ruayou.common.api_interface.config_center.ConfigChangeListener;
import com.ruayou.common.config.GlobalConfig;
import com.ruayou.common.utils.YamlUtils;
import lombok.extern.log4j.Log4j2;
import java.util.concurrent.Executor;
/**
 * @Author：ruayou
 * @Date：2024/1/29 22:37
 * @Filename：NacosConfigCenter
 */
@Log4j2
public class NacosConfigCenter implements ConfigCenter {
    /**
     * 服务端地址
     */
    private String serverAddr;
    /**
     * 环境
     */
    private String env;

    private ConfigService configService;
    private boolean isInit=false;

    @Override
    public void init(String serverAddr, String env) {
        if (this.isInit)return;
        this.serverAddr = serverAddr;
        this.env = env;
        try {
            this.configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        this.isInit=true;
    }

    /**
     * 根据dataId对配置中心的文件进行订阅
     * @param dataId
     * @param listener  配置变更监听器
     */
    @Override
    public void subscribeConfigChange(String dataId, ConfigChangeListener listener) {
        try {
            String configStr = configService.getConfig(dataId, env, 5000);
            log.debug("config from nacos: {}", configStr);
            GlobalConfig config = YamlUtils.parseYaml(configStr, GlobalConfig.class);
            listener.onConfigChange(config);
            //监听变化
            configService.addListener(dataId, env, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }
                @Override
                public void receiveConfigInfo(String configInfo) {
                    //配置发生动态变更
                    log.debug("config from nacos: {}", configInfo);
                    GlobalConfig config = YamlUtils.parseYaml(configInfo, GlobalConfig.class);
                    listener.onConfigChange(config);
                }
            });
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}

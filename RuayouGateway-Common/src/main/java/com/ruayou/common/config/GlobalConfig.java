package com.ruayou.common.config;
import com.ruayou.common.api_interface.Config;
import com.ruayou.common.utils.YamlUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author：ruayou
 * @Date：2024/1/30 21:54
 * @Filename：GloablConfig
 */
@Data
@Slf4j

public class GlobalConfig implements Config {
    private static GlobalConfig INSTANCE;
    public static String dataId = "GlobalConfig";
    public static String version = "1.0.0";
    private NettyServerConfig nettyServerConfig = new NettyServerConfig();
    private HttpClientConfig httpClientConfig = new HttpClientConfig();
    private RegisterAndConfigCenterConfig registerAndConfigCenterConfig = new RegisterAndConfigCenterConfig();
    private DisruptorConfig disruptorConfig= new DisruptorConfig();
    public static GlobalConfig getConfig() {
        if (INSTANCE == null){
            FileInputStream inputStream = null;
            INSTANCE = new GlobalConfig();
            try {
                // 使用相对路径加载配置文件
                inputStream = new FileInputStream("register-config.yaml");
                RegisterAndConfigCenterConfig nacosCon = YamlUtils.parseYaml(inputStream, RegisterAndConfigCenterConfig.class);
                if(nacosCon!=null){
                    INSTANCE.setRegisterAndConfigCenterConfig(nacosCon);
                    log.info("Load register center connection info success from config：{}",nacosCon);
                }

            } catch (IOException e) {
                log.error("Didn't find register center connection config，using default connection:{}",INSTANCE.getRegisterAndConfigCenterConfig());
            }

        }
        return INSTANCE;
    }
    public static void saveConfig(GlobalConfig config) {
        INSTANCE = config;
    }
}



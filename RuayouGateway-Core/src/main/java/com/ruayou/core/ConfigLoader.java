package com.ruayou.core;

import com.ruayou.common.config.GlobalConfig;
import com.ruayou.common.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;


/**
 * @Author：ruayou
 * @Date：2024/4/4 17:13
 * @Filename：ConfigLoader
 */
@Slf4j
public class ConfigLoader {
    private static final String CONFIG_FILE = "gateway.yaml";
    private static final ConfigLoader INSTANCE = new ConfigLoader();
    private ConfigLoader() {}
    public static ConfigLoader getInstance() {
        return INSTANCE;
    }
    private GlobalConfig globalConfig;
    public static GlobalConfig getGlobalConfig() {
        return INSTANCE.globalConfig;
    }

    public GlobalConfig load(String args[]) {
        //配置对象对默认值
        globalConfig = new GlobalConfig();

        //配置文件
        loadFromConfigFile();
//
//        //环境变量
//        loadFromEnv();
//
//        //jvm参数
//        loadFromJvm();
//
//        //运行参数
//        loadFromArgs(args);

        return globalConfig;
    }

    private void loadFromConfigFile() {
        InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream != null) {

            try {
                GlobalConfig config = YamlUtils.parseYaml(inputStream, GlobalConfig.class);
            } catch (YAMLException e) {
                log.warn("load config file {} error", CONFIG_FILE, e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        //
                    }
                }
            }
        }
    }

}

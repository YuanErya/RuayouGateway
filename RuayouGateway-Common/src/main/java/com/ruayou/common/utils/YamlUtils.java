package com.ruayou.common.utils;

import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import java.io.Reader;

/**
 * @Author：ruayou
 * @Date：2024/1/30 21:10
 * @Filename：YamlUtils
 * 用于解析Yaml的工具
 */
@Log4j2
public class YamlUtils {
    private final static Yaml yaml ;
    static {
        DumperOptions dumperOptions=new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml=new Yaml(dumperOptions);
    }

    public static <T> T parseYaml(String yamlStr,Class<T> clazz) {
        try {
            return yaml.loadAs(yamlStr, clazz);
        }catch (YAMLException e){
            log.error("Yaml text parsing error!");
        }
        return null;
    }


    public static <T> T parseYaml(Reader reader,Class<T> clazz) {
        try {
            return yaml.loadAs(reader, clazz);
        }catch (YAMLException e){
            log.error("Yaml text parsing error!");
        }
        return null;
    }

    public static String toYaml(Object obj) {
        return yaml.dump(obj);
    }
}

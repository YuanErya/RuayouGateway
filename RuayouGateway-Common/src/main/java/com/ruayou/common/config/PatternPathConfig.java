package com.ruayou.common.config;

import lombok.Data;
import java.util.HashMap;
import java.util.Set;

/**
 * @Author：ruayou
 * @Date：2024/1/31 23:30
 * @Filename：PatternPathConfig
 */
@Data
public class PatternPathConfig {
    public static String dataId = "pattern";
    private static PatternPathConfig INSTANCE = new PatternPathConfig();
    private HashMap<String,String> pattern =new HashMap<>();
    public static PatternPathConfig getConfig(){
        return INSTANCE;
    }
    public static void saveConfig(PatternPathConfig config){
        INSTANCE=config;
    }
    public Set<String> getPatternSet(){
        return pattern.keySet();
    }
}

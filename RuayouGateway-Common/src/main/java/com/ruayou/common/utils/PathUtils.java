package com.ruayou.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author：ruayou
 * @Date：2024/1/31 23:16
 * @Filename：PathUtils
 */
public class PathUtils {
    /**
     * 精确匹配：使用完整的路径进行精确匹配，例如 /users/123，只匹配该路径。
     * 通配符匹配：使用 * 通配符表示任意字符序列（除了斜杠 /），例如 /users/*，匹配 /users/ 后面的任意字符序列。
     * @param urlPath 待匹配路径
     * @param pattern 匹配规则
     * @return
     */
    public static boolean isMatch(String urlPath, String pattern) {
        // 转义斜杠和点号，因为它们在正则表达式中有特殊意义
        pattern = pattern.replace("/", "\\/").replace(".", "\\.");
        // 将星号(*)替换为匹配任意字符的正则表达式
        pattern = pattern.replace("*", ".*");
        // 构建正则表达式
        String regex = "^" + pattern + "$";
        // 创建Pattern对象，表示编译后的正则表达式
        Pattern p = Pattern.compile(regex);
        // 创建Matcher对象，用于进行匹配操作
        Matcher m = p.matcher(urlPath);
        // 返回匹配结果
        return m.matches();
    }

}

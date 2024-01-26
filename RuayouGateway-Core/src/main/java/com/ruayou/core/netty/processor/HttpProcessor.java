package com.ruayou.core.netty.processor;

/**
 * @Author：ruayou
 * @Date：2024/1/26 23:44
 * @Filename：HttpProcesser
 */
public interface HttpProcessor {
    void process();
    void start();
    void close();
}

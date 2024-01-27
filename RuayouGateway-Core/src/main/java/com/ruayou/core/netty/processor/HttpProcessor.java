package com.ruayou.core.netty.processor;

import com.ruayou.core.context.HttpRequestWrapper;

/**
 * @Author：ruayou
 * @Date：2024/1/26 23:44
 * @Filename：HttpProcesser
 */
public interface HttpProcessor {
    void process(HttpRequestWrapper httpRequestWrapper);
    void start();
    void close();
}

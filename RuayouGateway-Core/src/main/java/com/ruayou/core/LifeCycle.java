package com.ruayou.core;

/**
 * @Author：ruayou
 * @Date：2024/1/26 19:06
 * @Filename：LifeCycle
 */
public interface LifeCycle {
    void init();
    void start();
    void close();

    void restart();
}

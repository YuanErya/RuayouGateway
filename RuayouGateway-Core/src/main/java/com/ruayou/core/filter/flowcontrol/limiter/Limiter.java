package com.ruayou.core.filter.flowcontrol.limiter;


/**
 * @Author：ruayou
 * @Date：2024/3/7 21:46
 * @Filename：Limiter
 */
public interface Limiter {
    boolean tryPass(int permits);

    boolean isFit(String type);
}

package com.ruayou.core.filter.flowcontrol.limiter;

/**
 * @Author：ruayou
 * @Date：2024/3/7 23:00
 * @Filename：CloudCountLimiter
 */
public class CloudCountLimiter implements Limiter{

    @Override
    public boolean tryPass() {
        return true;
    }
}

package com.ruayou.core.filter.flowcontrol.limiter;

import com.ruayou.common.constant.FilterConst;

/**
 * @Author：ruayou
 * @Date：2024/3/7 23:00
 * @Filename：CloudCountLimiter
 */
public class CloudCountLimiter implements Limiter{

    public CloudCountLimiter(){}

    @Override
    public boolean tryPass(int permits) {
        return true;
    }

    @Override
    public boolean isFit(String type) {
        return FilterConst.FLOW_CTL_FILTER_MODE_CLOUD.equals(type);
    }
}

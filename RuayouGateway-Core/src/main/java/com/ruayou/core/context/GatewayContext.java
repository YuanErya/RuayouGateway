package com.ruayou.core.context;

import com.ruayou.common.config.FilterRule;
import com.ruayou.core.context.request.GatewayRequest;
import com.ruayou.core.context.response.GatewayResponse;
import io.netty.channel.ChannelHandlerContext;


/**
 * @Author：ruayou
 * @Date：2024/2/1 23:56
 * @Filename：GatewayContext
 */

public class GatewayContext extends Context{
    /**
     * 服务的唯一ID
     */
    private String uniqueId;

    private GatewayRequest request;

    private GatewayResponse response;

    private boolean gray;

    private int currentRetryCount;

    private FilterRule filterRule;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public GatewayRequest getRequest() {
        return request;
    }

    public void setRequest(GatewayRequest request) {
        this.request = request;
    }

    public GatewayResponse getResponse() {
        return response;
    }

    public void setResponse(GatewayResponse response) {
        this.response = response;
    }

    public boolean isGray() {
        return gray;
    }

    public void setGray(boolean gray) {
        this.gray = gray;
    }

    public int getCurrentRetryCount() {
        return currentRetryCount;
    }

    public void setCurrentRetryCount(int currentRetryCount) {
        this.currentRetryCount = currentRetryCount;
    }

    public FilterRule getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(FilterRule filterRule) {
        this.filterRule = filterRule;
    }

    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive,
                          GatewayRequest request, FilterRule filterRule, int currentRetryCount) {
        super(protocol, nettyCtx, keepAlive);
        this.request = request;
        this.uniqueId=request.getUniqueId();
        this.currentRetryCount = currentRetryCount;
        this.filterRule=filterRule;
    }
}

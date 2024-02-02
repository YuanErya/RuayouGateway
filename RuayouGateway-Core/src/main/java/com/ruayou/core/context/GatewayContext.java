package com.ruayou.core.context;

import com.ruayou.core.context.request.GatewayRequest;
import com.ruayou.core.context.response.GatewayResponse;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.codec.language.bm.Rule;


/**
 * @Author：ruayou
 * @Date：2024/2/1 23:56
 * @Filename：GatewayContext
 */
public class GatewayContext extends Context{

    private GatewayRequest request;

    private GatewayResponse response;

    private boolean gray;

    private int currentRetryTimes;
    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive,
                          GatewayRequest request,  int currentRetryTimes) {
        super(protocol, nettyCtx, keepAlive);
        this.request = request;
        this.currentRetryTimes = currentRetryTimes;
    }
}

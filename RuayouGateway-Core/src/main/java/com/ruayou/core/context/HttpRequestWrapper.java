package com.ruayou.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

/**
 * @Author：ruayou
 * @Date：2024/1/27 20:27
 * @Filename：HttpRequestWrapper
 */
@Data
public class HttpRequestWrapper {
    private FullHttpRequest request;
    private ChannelHandlerContext ctx;
    public HttpRequestWrapper(FullHttpRequest request,ChannelHandlerContext ctx){
        this.ctx=ctx;
        this.request=request;
    }
}

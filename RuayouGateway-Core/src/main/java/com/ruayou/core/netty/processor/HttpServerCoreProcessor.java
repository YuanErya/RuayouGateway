package com.ruayou.core.netty.processor;

import com.ruayou.core.context.HttpRequestWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @Author：ruayou
 * @Date：2024/1/26 23:42
 * @Filename：HttpServerProcessor
 */
public class HttpServerCoreProcessor implements HttpProcessor{
    @Override
    public void process(HttpRequestWrapper httpRequestWrapper) {
        FullHttpRequest request = httpRequestWrapper.getRequest();
        ChannelHandlerContext ctx = httpRequestWrapper.getCtx();

    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }
}

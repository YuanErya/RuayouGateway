package com.ruayou.core.netty.processor;

import com.ruayou.core.context.HttpRequestWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.log4j.Log4j2;

/**
 * @Author：ruayou
 * @Date：2024/1/26 23:42
 * @Filename：HttpServerProcessor
 */
@Log4j2
public class HttpServerCoreProcessor implements HttpProcessor{
    @Override
    public void process(HttpRequestWrapper httpRequestWrapper) {
        log.debug("processing request {}",httpRequestWrapper.getRequest());
        FullHttpRequest request = httpRequestWrapper.getRequest();
        ChannelHandlerContext ctx = httpRequestWrapper.getCtx();

    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public void restart() {

    }
}

package com.ruayou.core.netty.handler;

import com.ruayou.core.context.HttpRequestWrapper;
import com.ruayou.core.netty.processor.HttpProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Author：ruayou
 * @Date：2024/1/26 20:27
 * @Filename：HttpServerHandler
 */
@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private final HttpProcessor processor;
    public HttpServerHandler(HttpProcessor processor){
        this.processor=processor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        processor.process(new HttpRequestWrapper(request,ctx));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        // 打印自定义消息，实际使用时应该记录日志或进行更复杂的异常处理
        //log.error(cause.getMessage());
    }

}

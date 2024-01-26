package com.ruayou.core.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.Charset;

/**
 * @Author：ruayou
 * @Date：2024/1/26 20:27
 * @Filename：HttpServerHandler
 */
@Log4j2
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        //调试打印请求的详细信息
        log.debug("\nHEADER:\n{} \nBODY:\n{}",request,request.content().toString(Charset.defaultCharset()));
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(((FullHttpRequest) msg).protocolVersion(), HttpResponseStatus.OK);
        response.content().writeBytes("success".getBytes(Charset.defaultCharset()));
        ctx.writeAndFlush(response);
        ctx.close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        // 打印自定义消息，实际使用时应该记录日志或进行更复杂的异常处理
        log.error(cause.getMessage());
    }

}

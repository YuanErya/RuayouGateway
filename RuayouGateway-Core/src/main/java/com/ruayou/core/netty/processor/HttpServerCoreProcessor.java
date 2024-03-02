package com.ruayou.core.netty.processor;

import com.ruayou.common.enums.ResponseCode;
import com.ruayou.common.exception.GatewayException;
import com.ruayou.common.exception.ServiceNotFoundException;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.HttpRequestWrapper;
import com.ruayou.core.filter.FilterChainFactory;
import com.ruayou.core.filter.GatewayFilterChainFactory;
import com.ruayou.core.helper.RequestHelper;
import com.ruayou.core.helper.ResponseHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;

/**
 * @Author：ruayou
 * @Date：2024/1/26 23:42
 * @Filename：HttpServerProcessor
 */
@Log4j2
public class HttpServerCoreProcessor implements HttpProcessor{
    private final FilterChainFactory filterChainFactory = GatewayFilterChainFactory.getFactory();
    @Override
    public void process(HttpRequestWrapper httpRequestWrapper) {
        log.debug("processing request {}",httpRequestWrapper.getRequest());
        FullHttpRequest request = httpRequestWrapper.getRequest();
        ChannelHandlerContext ctx = httpRequestWrapper.getCtx();
        try{
            GatewayContext gatewayContext = RequestHelper.buildContext(request, ctx);
            filterChainFactory.buildFilterChain(gatewayContext).doFilters(gatewayContext);
        }
        catch (GatewayException e){
            log.error("发现异常{}",e.getMessage());
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        }
        catch (Exception e){
            log.error("发现异常{}",e.getMessage());
            //log.error("处理错误 {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }

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


    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE); // 发送响应后关闭通道。
        ReferenceCountUtil.release(request); // 释放与请求相关联的资源。
    }
}

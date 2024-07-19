package com.ruayou.core.filter.asyn;

import com.ruayou.common.exception.AsyncResponseException;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.request.GatewayRequest;
import com.ruayou.core.context.response.GatewayResponse;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.helper.ResponseHelper;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

import static com.ruayou.common.constant.FilterConst.*;
import static com.ruayou.common.enums.ResponseCode.ASYNC_RESPONSE_ERROR;

/**
 * @Author：ruayou
 * @Date：2024/7/13 16:58
 * @Filename：AsynGetFilter
 * 用于处理回调的响应
 */
@GFilter(id= ASYNC_GET_FILTER_ID,
        name = ASYNC_GET_FILTER_NAME,
        order =ASYNC_GET_FILTER_ORDER )
@Slf4j
public class AsyncGetFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        GatewayRequest ctxRequest = ctx.getRequest();
        String path = ctxRequest.getPath();
        if (!ASYNC_CALLBACK_URL.equals(path)) {
            return ;
        }
        HttpHeaders headers = ctxRequest.getHeaders();
        String callbackId = headers.get(CALLBACK_ID);
        if (callbackId == null) {
            throw new AsyncResponseException(ASYNC_RESPONSE_ERROR);
        }
        GatewayResponse response = AsyncResponseManager.getAsyncResponse(callbackId);
        if (response == null) {
            throw new AsyncResponseException(ASYNC_RESPONSE_ERROR);
        }
        ctx.setResponse(response);
        ctx.written();
        ResponseHelper.writeResponse(ctx);
        log.debug("Async get response request, callback:{}",callbackId);
        ctx.terminated();//标记结束过滤器链条的执行
    }

    @Override
    public int getOrder() {
        return Filter.super.getOrder();
    }
}

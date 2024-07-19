package com.ruayou.core.filter.asyn;

import com.ruayou.common.enums.ResponseCode;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.request.GatewayRequest;
import com.ruayou.core.context.response.GatewayResponse;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.helper.AsyncHttpHelper;
import com.ruayou.core.helper.ResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ruayou.common.constant.FilterConst.*;

/**
 * @Author：ruayou
 * @Date：2024/7/13 16:30
 * @Filename：AsynFilter
 */
@GFilter(id = ASYNC_FILTER_ID,
        name = ASYNC_FILTER_NAME,
        order = ASYNC_FILTER_ORDER)
@Slf4j
public class AsyncFilter implements Filter {

    private static final String TRUE = "true";


    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        GatewayRequest ctxRequest = ctx.getRequest();
        //校验请求头是否是异步调用
        String async = ctxRequest.getHeaders().get("async");
        if (!TRUE.equals(async)) {
            //正常请求继续路由过滤器
            return;
        }
        Request request = ctxRequest.build();
        UUID uuid = UUID.randomUUID();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);
        future.whenComplete((response, throwable) -> {
            complete(uuid,ctx, request, response, throwable);
        });

        //响应写入回调地址
        HashMap<String, String> map = new HashMap<>();
        map.put(CALLBACK, ASYNC_CALLBACK_URL);
        map.put(CALLBACK_ID, uuid.toString());
        ctx.setResponse(GatewayResponse.buildGatewayResponse(map));
        ctx.written();
        ResponseHelper.writeResponse(ctx);
        log.debug("Async request:{}:{} callbackId:{}", ctxRequest.getMethod(), ctxRequest.getPath(), uuid.toString());
        ctx.terminated();//标记结束过滤器链条的执行
    }

    private void complete(UUID uuid,GatewayContext ctx, Request request, Response response, Throwable throwable) {
        ctx.releaseRequest();
        if (Objects.nonNull(throwable)) {
            AsyncResponseManager.putAsyncResponse(uuid.toString(),GatewayResponse.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
            return;
        }
        //保存响应
        AsyncResponseManager.putAsyncResponse(uuid.toString(),GatewayResponse.buildGatewayResponse(response));
    }


}

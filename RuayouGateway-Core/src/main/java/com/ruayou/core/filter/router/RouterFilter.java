package com.ruayou.core.filter.router;
import com.ruayou.common.enums.ResponseCode;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.response.GatewayResponse;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import com.ruayou.core.helper.AsyncHttpHelper;
import com.ruayou.core.helper.ResponseHelper;
import lombok.extern.log4j.Log4j2;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import static com.ruayou.common.constant.FilterConst.*;

/**
 * @Author：ruayou
 * @Date：2024/1/29 18:15
 * @Filename：RouterFilter
 */
@Log4j2
@GFilter(id = ROUTER_FILTER_ID, name = ROUTER_FILTER_NAME, order = ROUTER_FILTER_ORDER)
public class RouterFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        route(ctx);
    }

    private CompletableFuture<Response> route(GatewayContext ctx) {
        Request request = ctx.getRequest().build();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);
        future.whenComplete((response, throwable) -> {
            complete(ctx,request,response,throwable);
        });
        return future;
    }

    private void complete(GatewayContext ctx, Request request,Response response, Throwable throwable) {
        ctx.releaseRequest();
        int currentRetryCount = ctx.getCurrentRetryCount();
        int retryCount =ctx.getFilterRule().getRetryConfig().getRetryCount();
        if ((throwable instanceof TimeoutException || throwable instanceof IOException) && currentRetryCount <= retryCount)
        {
            //&& !hystrixConfig.isPresent()
            //请求重试
            doRetry(ctx, currentRetryCount);
            return;
        }
        try{
            if(Objects.nonNull(throwable)){
                ctx.setThrowable(new RuntimeException(ResponseCode.HTTP_RESPONSE_ERROR.getMessage()));
                ctx.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
            }else {//请求正常
                ctx.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        }catch (Exception e){
            ctx.setThrowable(new RuntimeException(ResponseCode.INTERNAL_ERROR.getMessage()));
            ctx.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.INTERNAL_ERROR));
            log.error("complete error", e);
        } finally {
            ctx.written();//设置状态
            ResponseHelper.writeResponse(ctx);
            log.info("{} {} {} {} {} {} {}",
                    System.currentTimeMillis() - ctx.getRequest().getBeginTime(),
                    ctx.getRequest().getClientIp(),
                    ctx.getRequest().getUniqueId(),
                    ctx.getRequest().getMethod(),
                    ctx.getRequest().getPath(),
                    ctx.getResponse().getHttpResponseStatus().code(),
                    ctx.getResponse().getFutureResponse().getResponseBodyAsBytes().length);
        }

    }

    private void doRetry(GatewayContext ctx, int retryTime) {
        log.debug("请求异常，正在重试({})", retryTime);
        ctx.setCurrentRetryCount(retryTime + 1);
        try {
            doFilter(ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

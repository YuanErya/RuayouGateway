package com.ruayou.core.helper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ruayou.common.config.FilterRule;
import com.ruayou.common.config.ServiceAndInstanceManager;
import com.ruayou.common.constant.CommonConst;
import com.ruayou.common.constant.ServiceConst;
import com.ruayou.common.entity.ServiceDefinition;
import com.ruayou.common.exception.ServiceNotFoundException;
import com.ruayou.common.utils.PathUtils;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.request.GatewayRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ruayou.common.enums.ResponseCode.SERVICE_DEFINITION_NOT_FOUND;

/**
 * @Author：ruayou
 * @Date：2024/2/6 17:07
 * @Filename：RequestHelper
 */
public class RequestHelper {

    private static final Cache<String,String> serviceIdCache= Caffeine.newBuilder().recordStats().expireAfterWrite(10,
            TimeUnit.MINUTES).build();
    public static void cleanServiceIdCache(){
        serviceIdCache.invalidateAll();
    }

    public static GatewayContext buildContext(FullHttpRequest request, ChannelHandlerContext ctx) {
        GatewayRequest gateWayRequest = buildGatewayRequest(request, ctx);
        String path=gateWayRequest.getPath();
        FilterRule filterRule = ServiceAndInstanceManager.getManager().getRuleByPath(path);

        String serviceId=serviceIdCache.get(path, k-> getServiceIdByPath(path,filterRule));

        ServiceDefinition serviceDefinition =
                ServiceAndInstanceManager.getManager().getServiceDefinition(serviceId);
        if(serviceDefinition==null){
            //注册中心没有找到对应的服务。
            throw new ServiceNotFoundException(SERVICE_DEFINITION_NOT_FOUND);
        }
        return new GatewayContext(serviceDefinition.getProtocol(), ctx,
                HttpUtil.isKeepAlive(request), gateWayRequest,serviceId, filterRule, 0);
    }

    private static String getServiceIdByPath(String path,FilterRule filterRule){
        Map<String, String> patterns = filterRule.getPatterns();
        //待加缓存映射id
        for (String pattern : patterns.keySet()) {
            if (PathUtils.isMatch(path, pattern)) {
                return patterns.get(pattern);
            }
        }
        throw new ServiceNotFoundException(SERVICE_DEFINITION_NOT_FOUND);
    }


    private static GatewayRequest buildGatewayRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        HttpHeaders headers = fullHttpRequest.headers();
        String uri = fullHttpRequest.uri();
        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null :
                HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);
        return new GatewayRequest(charset, clientIp, host, uri, method,
                contentType, headers, fullHttpRequest);
    }

    /**
     * 获取客户端ip
     */
    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        String xForwardedValue = request.headers().get(CommonConst.HTTP_FORWARD_SEPARATOR);
        String clientIp = null;
        if (StringUtils.isNotEmpty(xForwardedValue)) {
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if (values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }
        if (clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }
}

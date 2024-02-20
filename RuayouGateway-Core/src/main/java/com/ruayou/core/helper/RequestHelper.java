package com.ruayou.core.helper;

import com.ruayou.common.config.FilterRule;
import com.ruayou.common.config.ServiceAndInstanceManager;
import com.ruayou.common.constant.CommonConst;
import com.ruayou.common.constant.ServiceConst;
import com.ruayou.common.entity.ServiceDefinition;
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

/**
 * @Author：ruayou
 * @Date：2024/2/6 17:07
 * @Filename：RequestHelper
 */
public class RequestHelper {

    public static GatewayContext buildContext(FullHttpRequest request, ChannelHandlerContext ctx) {
        GatewayRequest gateWayRequest = buildGatewayRequest(request, ctx);

        FilterRule filterRule=null;
        if (gateWayRequest.getUniqueId()==null) {
            Map<String, String> patterns = filterRule.getPatterns();
            patterns.keySet().forEach((pattern)->{
                if (PathUtils.isMatch(gateWayRequest.getPath(),pattern)) {
                    gateWayRequest.setUniqueId(patterns.get(pattern));
                }
            });
        }//因该在构建请求的时候设置，后期改进。
        ServiceDefinition serviceDefinition =
                ServiceAndInstanceManager.getManager().getServiceDefinition(gateWayRequest.getUniqueId());
        return new GatewayContext(serviceDefinition.getProtocol(), ctx,
                HttpUtil.isKeepAlive(request), gateWayRequest, filterRule, 0);
    }

    private static GatewayRequest buildGatewayRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        HttpHeaders headers = fullHttpRequest.headers();
        String uniqueId = headers.get(ServiceConst.UNIQUE_ID);//待定
        String uri = fullHttpRequest.uri();
        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null :
                HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);
        return new GatewayRequest(uniqueId, charset, clientIp, host, uri, method,
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

package com.ruayou.core.filter.loadbalance;

import com.ruayou.common.entity.ServiceInstance;
import com.ruayou.common.exception.InstanceException;
import com.ruayou.core.context.GatewayContext;
import com.ruayou.core.context.request.GatewayRequest;
import com.ruayou.core.filter.Filter;
import com.ruayou.core.filter.GFilter;
import lombok.extern.log4j.Log4j2;
import static com.ruayou.common.constant.FilterConst.*;
import static com.ruayou.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @Author：ruayou
 * @Date：2024/2/5 23:33
 * @Filename：LoadBalanceFilter
 */
@Log4j2
@GFilter(id = LOAD_BALANCE_FILTER_ID, name = LOAD_BALANCE_FILTER_NAME, order = LOAD_BALANCE_FILTER_ORDER)
public class LoadBalanceFilter implements Filter {


    /**
     * 注意ctx.getRequest()的构建时候。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        String serviceId = ctx.getServiceId();
        LoadBalanceStrategy loadBalanceStrategy =getLoadBalanceStrategy(ctx);
        ServiceInstance instance=loadBalanceStrategy.choose(ctx.getServiceId(),ctx.getFilterRule().getVersion(),ctx.isGray());
        log.debug("选择实例：{}",instance.toString());
        GatewayRequest request = ctx.getRequest();
        if (instance != null && request != null) {
            String host = instance.getIp() + ":" + instance.getPort();
            request.setModifyHost(host);
        }else {
            log.warn("No instance available for :{}", serviceId);
            throw new InstanceException(SERVICE_INSTANCE_NOT_FOUND);
        }
    }


    /**
     * 根据配置获取负载均衡器
     *
     * @param ctx
     * @return
     */
    public LoadBalanceStrategy getLoadBalanceStrategy(GatewayContext ctx) {
return RandomLoadBalanceStrategy.getInstance(ctx.getServiceId());
    }
}

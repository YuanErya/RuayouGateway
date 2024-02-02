package com.ruayou.core.filter;

import com.ruayou.core.context.GatewayContext;

/**
 * @Author：ruayou
 * @Date：2024/1/29 18:15
 * @Filename：Filter
 */
public interface Filter {
    void doFilter(GatewayContext ctx)throws  Exception;

    default int getOrder(){
        GFilter annotation = this.getClass().getAnnotation(GFilter.class);
        if(annotation != null){
            return annotation.order();
        }
        return Integer.MAX_VALUE;
    };

}

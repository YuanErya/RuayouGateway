package com.ruayou.core.filter;

import java.lang.annotation.*;

/**
 * @Author：ruayou
 * @Date：2024/1/29 18:22
 * @Filename：GFilter
 * 用于标记过滤器，同时设置过滤器优先级
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GFilter {
    /**
     * 过滤器ID
     * @return
     */
    String id();

    /**
     * 过滤器名称
     * @return
     */
    String name() default "";

    /**
     * 优先级
     * @return
     */
    int order() default 0;
}

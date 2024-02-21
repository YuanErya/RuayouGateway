package com.ruayou.client.annotation;

import java.lang.annotation.*;

/**
 * @Author：ruayou
 * @Date：2024/2/21 16:20
 * @Filename：RGService
 *用于服务的注册
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RGService {
    String serviceId();

    String version() default "1.0.0";

    String protocol() default "http";

    String[] patternPath();
}

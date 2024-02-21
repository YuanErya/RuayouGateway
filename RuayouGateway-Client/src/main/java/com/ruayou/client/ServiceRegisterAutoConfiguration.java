package com.ruayou.client;

import com.ruayou.client.manager.SpringClientRegisterManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.Servlet;

/**
 * @Author：ruayou
 * @Date：2024/2/21 16:29
 * @Filename：ServiceRegisterAutoConfiguration
 */

@Configuration
@EnableConfigurationProperties(AutoRegisterProperties.class)
@ConditionalOnProperty(prefix = "ruayou.register", name = {"address"})
public class ServiceRegisterAutoConfiguration {
    @Resource
    private AutoRegisterProperties properties;

    @Bean
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean(SpringClientRegisterManager.class)
    public SpringClientRegisterManager springMVCClientRegisterManager() {
        return new SpringClientRegisterManager(properties);
    }
}

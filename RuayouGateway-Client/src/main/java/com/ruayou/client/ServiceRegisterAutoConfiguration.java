package com.ruayou.client;

import com.ruayou.client.manager.SpringClientRegisterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @Author：ruayou
 * @Date：2024/2/21 16:29
 * @Filename：ServiceRegisterAutoConfiguration
 */

@Configuration
@EnableConfigurationProperties(AutoRegisterProperties.class)
@ConditionalOnProperty(prefix = "register", name = {"address"})
public class ServiceRegisterAutoConfiguration {
    @Autowired
    private AutoRegisterProperties properties;

    @Autowired
    private ServerProperties serverProperties;

    @Bean
    @ConditionalOnClass({DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean(SpringClientRegisterManager.class)
    public SpringClientRegisterManager springMVCClientRegisterManager() {
        return new SpringClientRegisterManager(properties,serverProperties);
    }
}

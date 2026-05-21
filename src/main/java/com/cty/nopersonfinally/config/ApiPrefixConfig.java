package com.cty.nopersonfinally.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置API路径前缀
 */
@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 添加/api前缀支持，让所有接口可以通过/api/xxx访问
        configurer.addPathPrefix("api", c -> true);
    }
}
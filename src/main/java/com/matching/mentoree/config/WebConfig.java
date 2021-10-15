package com.matching.mentoree.config;

import com.matching.mentoree.config.interceptor.AuthorityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorityInterceptor())
                .addPathPatterns("/program/**")
                .excludePathPatterns("/member/**", "/login", "/logout", "/", "/program/registry", "/program/info/**");
    }

}

package com.ISP392.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private FirstLoginInterceptor firstLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(firstLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/first-login", "/auth/send-otp", "/auth/verify-otp", "/auth/resend-otp",
                                   "/login", "/logout", "/assets/**", "/css/**", 
                                   "/js/**", "/images/**", "/static/**");
    }
} 
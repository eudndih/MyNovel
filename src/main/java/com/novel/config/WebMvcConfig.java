package com.novel.config;

import com.novel.interceptor.LoginInterceptor;
import com.novel.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final TokenInterceptor tokenInterceptor;
    private final LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先解析身份，所有接口都可以经过
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .order(0);

        // 再校验登录，仅拦截需要登录的接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/send-code"
                )
                .order(1);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/upload/**").addResourceLocations("File:D:\\code\\MyNovel\\src\\main\\resources\\upload\\");
    }
}

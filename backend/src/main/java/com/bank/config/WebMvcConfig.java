package com.bank.config;

import com.bank.config.JwtInterceptor;
import com.bank.rbac.RoleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册拦截器、静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final RoleInterceptor roleInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor, RoleInterceptor roleInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.roleInterceptor = roleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 解析拦截 - 所有路径都经过，解析 token 并设置用户属性
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/bankImg/**",
                        "/user/login",
                        "/user/register",
                        "/error"
                );

        // RBAC 角色拦截
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/bankImg/**",
                        "/user/login",
                        "/user/register",
                        "/user/logout",
                        "/product/list",
                        "/product/detail/**",
                        "/banner/list",
                        "/error"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 图片资源映射 - 同时支持 classpath 和本地文件系统
        registry.addResourceHandler("/bankImg/**")
                .addResourceLocations("classpath:/static/bankImg/", "file:src/main/resources/static/bankImg/");
    }
}

package com.bank.config;

import com.bank.common.NotLoginException;
import com.bank.config.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录拦截器
 * 拦截需要登录的接口，从请求头提取 token 并解析用户信息
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.parseToken(token);
                    request.setAttribute("userId", claims.getSubject());
                    request.setAttribute("phone", claims.get("phone"));
                    request.setAttribute("roleId", claims.get("roleId"));
                }
            } catch (Exception ignored) {
                // token 无效时不阻断，让 RoleInterceptor 判断
            }
        }
        return true;
    }
}

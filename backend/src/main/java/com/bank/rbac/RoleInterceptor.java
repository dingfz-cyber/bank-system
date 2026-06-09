package com.bank.rbac;

import com.bank.common.BusinessException;
import com.bank.common.NotLoginException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * RBAC 角色拦截器
 * 检查带 @RequireRole 注解的接口是否有对应角色权限
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RequireRole annotation = method.getMethodAnnotation(RequireRole.class);
        if (annotation == null) {
            annotation = method.getBeanType().getAnnotation(RequireRole.class);
        }
        if (annotation == null) {
            return true;
        }

        Object roleIdAttr = request.getAttribute("roleId");
        if (roleIdAttr == null) {
            throw new NotLoginException("未登录");
        }

        long roleId = Long.parseLong(roleIdAttr.toString());

        // 超级管理员（roleId=1）拥有所有权限
        if (roleId == 1L) {
            return true;
        }

        if (annotation.anyRole()) {
            return true;
        }

        for (String role : annotation.value()) {
            long requiredRoleId = getRoleIdByCode(role);
            if (roleId == requiredRoleId) {
                return true;
            }
        }

        throw new BusinessException(403, "无权限访问");
    }

    private long getRoleIdByCode(String roleCode) {
        return switch (roleCode.toUpperCase()) {
            case "SUPER_ADMIN" -> 1L;
            case "OPERATOR" -> 2L;
            case "USER" -> 3L;
            default -> throw new BusinessException("未知角色编码: " + roleCode);
        };
    }
}

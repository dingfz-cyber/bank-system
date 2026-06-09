package com.bank.rbac;

import java.lang.annotation.*;

/**
 * 角色权限注解 - 标注在 Controller 方法上
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 需要的角色编码，如 SUPER_ADMIN、OPERATOR
     */
    String[] value() default {};

    /**
     * 是否允许任意角色
     */
    boolean anyRole() default false;
}

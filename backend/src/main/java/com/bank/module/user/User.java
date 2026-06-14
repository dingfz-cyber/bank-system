package com.bank.module.user;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息实体
 */
@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    @JsonIgnore
    private String password;

    private String nickName;

    /** 真实姓名 */
    private String realName;

    /** 身份证号 */
    private String idCard;

    /** 交易密码（6位数字 BCrypt） */
    @JsonIgnore
    private String transactionPassword;

    /** 连续登录失败次数 */
    private Integer loginAttempts;

    /** 账号锁定截止时间 */
    private LocalDateTime lockedAt;

    /** 积分 */
    private Integer points;

    private Long roleId;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

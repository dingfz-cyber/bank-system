package com.bank.module.user.dto;

import lombok.Data;

/**
 * 登录成功返回 VO
 */
@Data
public class UserVo {

    private Long id;
    private String phone;
    private String nickName;
    private Long roleId;
    private Integer points;
    private String token;
}

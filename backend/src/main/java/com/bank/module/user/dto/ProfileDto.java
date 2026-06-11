package com.bank.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改个人信息请求 DTO
 */
@Data
public class ProfileDto {

    @NotBlank(message = "昵称不能为空")
    private String nickName;
}

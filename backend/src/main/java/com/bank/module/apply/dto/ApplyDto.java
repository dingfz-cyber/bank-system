package com.bank.module.apply.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 申请提交请求 DTO
 */
@Data
public class ApplyDto {

    @NotNull(message = "产品 ID 不能为空")
    private Long productId;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotNull(message = "申请类型不能为空")
    private Integer applyType;
}

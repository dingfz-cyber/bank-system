package com.bank.module.product;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品信息实体
 */
@Data
@TableName("bank_product")
public class BankProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String productName;

    private Integer productType;

    private BigDecimal rate;

    /** 起购金额 */
    private BigDecimal minAmount;

    /** 期限说明 */
    private String term;

    /** 风险等级：1=低 2=中 3=高 */
    private Integer riskLevel;

    /** 产品状态：0=正常 1=停售 */
    private Integer productStatus;

    /** 手续费说明 */
    private String feeDesc;

    private String intro;

    private String imgPath;

    private Integer sort;

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

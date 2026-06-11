package com.bank.module.card;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 银行卡实体
 */
@Data
@TableName("bank_card")
public class BankCard {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long applyId;

    private String cardNumber;

    /** 卡类型：1=借记卡 2=信用卡 */
    private Integer cardType;

    private BigDecimal creditLimit;

    private BigDecimal balance;

    private String cvv;

    private String expiryDate;

    /** 状态：0=正常 1=已冻结 2=已注销 */
    private Integer status;

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

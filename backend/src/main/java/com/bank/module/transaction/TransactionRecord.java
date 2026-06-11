package com.bank.module.transaction;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易流水实体
 */
@Data
@TableName("transaction_record")
public class TransactionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 付款卡 ID */
    private Long fromCardId;

    /** 收款账号/卡号 */
    private String toAccount;

    private BigDecimal amount;

    private BigDecimal fee;

    /** 交易类型：1=转账 2=缴费 3=理财 4=还款 */
    private Integer type;

    /** 状态：0=失败 1=成功 */
    private Integer status;

    private String remark;

    private LocalDateTime tradeTime;

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

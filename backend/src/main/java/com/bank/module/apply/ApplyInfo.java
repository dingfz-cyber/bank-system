package com.bank.module.apply;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请表实体
 */
@Data
@TableName("apply_info")
public class ApplyInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private String realName;

    private String phone;

    private Integer applyType;

    /** 贷款放款目标卡 ID */
    private Long cardId;

    /** 审核状态：0=待审核 1=已通过 2=已拒绝 */
    private Integer status;

    /** 审核备注/拒绝原因 */
    private String remark;

    /** 审批人 ID */
    private Long approverId;

    /** 审批时间 */
    private LocalDateTime approveTime;

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

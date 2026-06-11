package com.bank.module.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("login_record")
public class LoginRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String phone;
    private String ip;
    private String device;
    private LocalDateTime loginTime;
}

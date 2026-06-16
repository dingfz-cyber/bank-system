package com.bank.module.points;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("points_record")
public class PointsRecord {
    @TableId(type=IdType.AUTO) private Long id;
    private Long userId;
    private Integer points;
    private String reason;
    private LocalDateTime createTime;
}

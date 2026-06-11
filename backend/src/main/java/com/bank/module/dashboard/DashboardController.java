package com.bank.module.dashboard;

import com.bank.common.Result;
import com.bank.module.dashboard.dto.DashboardVo;
import com.bank.rbac.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板控制器
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取统计数据（管理员）
     */
    @GetMapping("/stats")
    @RequireRole({"SYS_ADMIN", "BIZ_ADMIN", "OPERATOR"})
    public Result<DashboardVo> stats() {
        return Result.success(dashboardService.getStats());
    }
}

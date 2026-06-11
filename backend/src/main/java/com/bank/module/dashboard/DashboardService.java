package com.bank.module.dashboard;

import com.bank.module.dashboard.dto.DashboardVo;

/**
 * 数据看板服务接口
 */
public interface DashboardService {

    /**
     * 获取统计数据
     */
    DashboardVo getStats();
}

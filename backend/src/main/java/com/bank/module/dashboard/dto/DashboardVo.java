package com.bank.module.dashboard.dto;

import lombok.Data;

/**
 * 数据看板 VO
 */
@Data
public class DashboardVo {

    /** 用户总数 */
    private long userCount;

    /** 产品总数 */
    private long productCount;

    /** 今日申请数 */
    private long todayApplyCount;

    /** 待审核申请数 */
    private long pendingCount;

    /** 已通过申请数 */
    private long approvedCount;

    /** 已拒绝申请数 */
    private long rejectedCount;

    /** 新闻公告数 */
    private long newsCount;

    /** 银行卡总数 */
    private long cardCount;

    /** 交易记录数 */
    private long transactionCount;

    /** 个人业务产品数 */
    private long productType1Count;

    /** 信用卡产品数 */
    private long productType2Count;

    /** 公司金融产品数 */
    private long productType3Count;

    /** 普惠金融产品数 */
    private long productType4Count;
}

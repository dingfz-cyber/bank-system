package com.bank.module.card;

import java.util.List;

/**
 * 银行卡服务接口
 */
public interface BankCardService {

    /**
     * 查询用户的所有银行卡
     */
    List<BankCard> listByUserId(Long userId);

    /**
     * 银行卡详情
     */
    BankCard detail(Long id);

    /**
     * 创建银行卡（审批通过时调用）
     */
    BankCard create(Long userId, Long applyId, Integer cardType);

    /**
     * 冻结银行卡
     */
    void freeze(Long id, Long userId);

    /**
     * 解冻银行卡
     */
    void unfreeze(Long id, Long userId);

    /**
     * 注销银行卡
     */
    void cancel(Long id, Long userId);
}

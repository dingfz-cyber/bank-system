package com.bank.module.transaction;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.math.BigDecimal;

/**
 * 交易服务接口
 */
public interface TransactionService {

    /**
     * 行内转账
     */
    void transfer(Long userId, Long fromCardId, String toAccount, BigDecimal amount, String transactionPassword, String remark);

    /**
     * 交易流水查询
     */
    IPage<TransactionRecord> list(Long userId, long page, long pageSize, Integer type, String startDate, String endDate);
}

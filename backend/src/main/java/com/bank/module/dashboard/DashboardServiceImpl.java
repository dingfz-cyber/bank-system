package com.bank.module.dashboard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.module.apply.ApplyInfo;
import com.bank.module.apply.ApplyInfoMapper;
import com.bank.module.card.BankCardMapper;
import com.bank.module.dashboard.dto.DashboardVo;
import com.bank.module.news.NewsMapper;
import com.bank.module.product.BankProduct;
import com.bank.module.product.BankProductMapper;
import com.bank.module.transaction.TransactionMapper;
import com.bank.module.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 数据看板服务实现
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final BankProductMapper bankProductMapper;
    private final ApplyInfoMapper applyInfoMapper;
    private final BankCardMapper bankCardMapper;
    private final TransactionMapper transactionMapper;
    private final NewsMapper newsMapper;

    @Override
    public DashboardVo getStats() {
        DashboardVo vo = new DashboardVo();

        // 用户总数
        vo.setUserCount(userMapper.selectCount(null));

        // 产品总数
        vo.setProductCount(bankProductMapper.selectCount(null));

        // 今日申请数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        vo.setTodayApplyCount(applyInfoMapper.selectCount(
                new LambdaQueryWrapper<ApplyInfo>()
                        .ge(ApplyInfo::getCreateTime, todayStart)
                        .le(ApplyInfo::getCreateTime, todayEnd)
        ));

        // 按状态统计申请数
        vo.setPendingCount(applyInfoMapper.selectCount(
                new LambdaQueryWrapper<ApplyInfo>().eq(ApplyInfo::getStatus, 0)
        ));
        vo.setApprovedCount(applyInfoMapper.selectCount(
                new LambdaQueryWrapper<ApplyInfo>().eq(ApplyInfo::getStatus, 1)
        ));
        vo.setRejectedCount(applyInfoMapper.selectCount(
                new LambdaQueryWrapper<ApplyInfo>().eq(ApplyInfo::getStatus, 2)
        ));

        vo.setNewsCount(newsMapper.selectCount(null));

        // 按类型统计产品数
        vo.setProductType1Count(bankProductMapper.selectCount(new LambdaQueryWrapper<BankProduct>().eq(BankProduct::getProductType, 1)));
        vo.setProductType2Count(bankProductMapper.selectCount(new LambdaQueryWrapper<BankProduct>().eq(BankProduct::getProductType, 2)));
        vo.setProductType3Count(bankProductMapper.selectCount(new LambdaQueryWrapper<BankProduct>().eq(BankProduct::getProductType, 3)));
        vo.setProductType4Count(bankProductMapper.selectCount(new LambdaQueryWrapper<BankProduct>().eq(BankProduct::getProductType, 4)));

        vo.setCardCount(bankCardMapper.selectCount(null));
        vo.setTransactionCount(transactionMapper.selectCount(null));

        return vo;
    }
}

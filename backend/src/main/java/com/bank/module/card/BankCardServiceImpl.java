package com.bank.module.card;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.common.BusinessException;
import com.bank.log.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 银行卡服务实现
 */
@Service
@RequiredArgsConstructor
public class BankCardServiceImpl implements BankCardService {

    private final BankCardMapper bankCardMapper;
    private final Random random = new Random();

    @Override
    public List<BankCard> listByUserId(Long userId) {
        LambdaQueryWrapper<BankCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BankCard::getUserId, userId).orderByDesc(BankCard::getCreateTime);
        return bankCardMapper.selectList(wrapper);
    }

    @Override
    public BankCard detail(Long id) {
        BankCard card = bankCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException("银行卡不存在");
        }
        return card;
    }

    @Override
    @OperationLog(value = "创建银行卡", type = "add")
    public BankCard create(Long userId, Long applyId, Integer cardType) {
        BankCard card = new BankCard();
        card.setUserId(userId);
        card.setApplyId(applyId);
        card.setCardNumber(generateCardNumber());
        card.setCardType(cardType);
        card.setCreditLimit(cardType == 2 ? new BigDecimal("10000") : BigDecimal.ZERO);
        card.setBalance(cardType == 2 ? new BigDecimal("5000") : new BigDecimal("10000")); // 演示初始余额
        card.setCvv(String.format("%03d", random.nextInt(1000)));
        card.setExpiryDate(LocalDate.now().plusYears(3).format(DateTimeFormatter.ofPattern("MM/yy")));
        card.setStatus(0);
        bankCardMapper.insert(card);
        return card;
    }

    @Override
    @OperationLog(value = "冻结银行卡", type = "freeze")
    public void freeze(Long id, Long userId) {
        BankCard card = checkOwnership(id, userId);
        if (card.getStatus() == 1) {
            throw new BusinessException("该卡已被冻结");
        }
        card.setStatus(1);
        bankCardMapper.updateById(card);
    }

    @Override
    @OperationLog(value = "解冻银行卡", type = "unfreeze")
    public void unfreeze(Long id, Long userId) {
        BankCard card = checkOwnership(id, userId);
        if (card.getStatus() != 1) {
            throw new BusinessException("该卡未被冻结");
        }
        card.setStatus(0);
        bankCardMapper.updateById(card);
    }

    @Override
    @OperationLog(value = "注销银行卡", type = "cancel")
    public void cancel(Long id, Long userId) {
        BankCard card = checkOwnership(id, userId);
        if (card.getStatus() == 2) throw new BusinessException("该卡已注销");
        card.setStatus(2);
        bankCardMapper.updateById(card);
    }

    private BankCard checkOwnership(Long id, Long userId) {
        BankCard card = bankCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException("银行卡不存在");
        }
        if (!card.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此卡");
        }
        return card;
    }

    /** 卡号：6228 + 13位时间戳后10位 + 5位随机 = 19位 */
    private String generateCardNumber() {
        String ts = String.valueOf(System.currentTimeMillis());
        String suffix = String.format("%05d", random.nextInt(100000));
        return "6228" + ts.substring(ts.length() - 10) + suffix;
    }
}

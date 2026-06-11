package com.bank.module.transaction;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.log.OperationLog;
import com.bank.module.card.BankCard;
import com.bank.module.card.BankCardMapper;
import com.bank.module.message.Message;
import com.bank.module.message.MessageMapper;
import com.bank.module.user.User;
import com.bank.module.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final BankCardMapper bankCardMapper;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    @OperationLog(value = "行内转账", type = "transfer")
    public void transfer(Long userId, Long fromCardId, String toAccount, BigDecimal amount, String transactionPassword, String remark) {
        // 1. 校验交易密码
        User user = userMapper.selectById(userId);
        if (user == null || user.getTransactionPassword() == null || user.getTransactionPassword().isEmpty()) {
            throw new BusinessException("请先设置交易密码");
        }
        if (!passwordEncoder.matches(transactionPassword, user.getTransactionPassword())) {
            throw new BusinessException("交易密码错误");
        }

        // 2. 校验付款卡
        BankCard fromCard = bankCardMapper.selectById(fromCardId);
        if (fromCard == null || !fromCard.getUserId().equals(userId)) {
            throw new BusinessException("付款卡不存在");
        }
        if (fromCard.getStatus() != 0) {
            throw new BusinessException("付款卡状态异常，无法转账");
        }

        // 3. 校验余额
        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        // 4. 校验收款卡存在
        LambdaQueryWrapper<BankCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BankCard::getCardNumber, toAccount);
        BankCard toCard = bankCardMapper.selectOne(wrapper);
        if (toCard == null) {
            throw new BusinessException("收款账号不存在");
        }

        // 5. 执行转账（扣款+入账）
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        bankCardMapper.updateById(fromCard);

        toCard.setBalance(toCard.getBalance().add(amount));
        bankCardMapper.updateById(toCard);

        // 6. 生成交易记录
        TransactionRecord record = new TransactionRecord();
        record.setUserId(userId);
        record.setFromCardId(fromCardId);
        record.setToAccount(toAccount);
        record.setAmount(amount);
        record.setFee(BigDecimal.ZERO);
        record.setType(1); // 转账
        record.setStatus(1); // 成功
        record.setRemark(remark != null ? remark : "");
        record.setTradeTime(LocalDateTime.now());
        transactionMapper.insert(record);

        // 收款方交易记录
        TransactionRecord recvRecord = new TransactionRecord();
        recvRecord.setUserId(toCard.getUserId());
        recvRecord.setFromCardId(toCard.getId());
        recvRecord.setToAccount(fromCard.getCardNumber());
        recvRecord.setAmount(amount);
        recvRecord.setFee(BigDecimal.ZERO);
        recvRecord.setType(1);
        recvRecord.setStatus(1);
        recvRecord.setRemark(remark != null ? remark : "");
        recvRecord.setTradeTime(LocalDateTime.now());
        transactionMapper.insert(recvRecord);

        // 发送消息通知—付款方
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setTitle("转账成功");
        msg.setContent("您已成功转账 ¥" + amount + " 至 " + toAccount);
        msg.setType("transaction");
        msg.setIsRead(0);
        messageMapper.insert(msg);

        // 发送消息通知—收款方
        Message recvMsg = new Message();
        recvMsg.setUserId(toCard.getUserId());
        recvMsg.setTitle("到账通知");
        recvMsg.setContent("您收到来自 " + user.getPhone() + " 的转账 ¥" + amount);
        recvMsg.setType("transaction");
        recvMsg.setIsRead(0);
        messageMapper.insert(recvMsg);
    }

    @Override
    public IPage<TransactionRecord> list(Long userId, long page, long pageSize, Integer type, String startDate, String endDate) {
        LambdaQueryWrapper<TransactionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransactionRecord::getUserId, userId);
        if (type != null && type > 0) {
            wrapper.eq(TransactionRecord::getType, type);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(TransactionRecord::getTradeTime, LocalDate.parse(startDate).atStartOfDay());
        } else {
            // 默认近3个月
            wrapper.ge(TransactionRecord::getTradeTime, LocalDate.now().minusMonths(3).atStartOfDay());
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(TransactionRecord::getTradeTime, LocalDate.parse(endDate).atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(TransactionRecord::getTradeTime);
        return transactionMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }
}

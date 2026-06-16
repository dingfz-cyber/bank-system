package com.bank.module.apply;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.log.OperationLog;
import com.bank.module.apply.dto.ApplyDto;
import com.bank.module.card.BankCardService;
import com.bank.module.message.Message;
import com.bank.module.message.MessageMapper;
import com.bank.module.card.BankCard;
import com.bank.module.card.BankCardMapper;
import com.bank.module.product.BankProduct;
import com.bank.module.product.BankProductMapper;
import com.bank.module.transaction.TransactionMapper;
import com.bank.module.transaction.TransactionRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申请服务实现
 */
@Service
@RequiredArgsConstructor
public class ApplyInfoServiceImpl implements ApplyInfoService {

    private final ApplyInfoMapper applyInfoMapper;
    private final BankCardService bankCardService;
    private final MessageMapper messageMapper;
    private final BankProductMapper bankProductMapper;
    private final TransactionMapper transactionMapper;
    private final BankCardMapper bankCardMapper;

    @Override
    @OperationLog(value = "提交申请", type = "add")
    public void submit(ApplyDto dto, Long userId) {
        ApplyInfo apply = new ApplyInfo();
        apply.setUserId(userId);
        apply.setProductId(dto.getProductId());
        apply.setRealName(dto.getRealName());
        apply.setPhone(dto.getPhone());
        apply.setApplyType(dto.getApplyType());
        apply.setCardId(dto.getCardId());
        applyInfoMapper.insert(apply);
    }

    @Override
    public IPage<ApplyInfo> page(long page, long pageSize, Long userId) {
        LambdaQueryWrapper<ApplyInfo> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ApplyInfo::getUserId, userId);
        }
        wrapper.orderByDesc(ApplyInfo::getCreateTime);
        return applyInfoMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public IPage<ApplyInfo> recyclePage(long page, long pageSize) {
        // 使用自定义 SQL 绕过 MP 逻辑删除过滤
        List<ApplyInfo> list = applyInfoMapper.selectDeletedList();
        long total = applyInfoMapper.selectDeletedCount();

        Page<ApplyInfo> result = new Page<>(page, pageSize, total);
        int fromIndex = (int) ((page - 1) * pageSize);
        int toIndex = Math.min(fromIndex + (int) pageSize, list.size());
        if (fromIndex < list.size()) {
            result.setRecords(list.subList(fromIndex, toIndex));
        } else {
            result.setRecords(List.of());
        }
        return result;
    }

    @Override
    @OperationLog(value = "恢复申请", type = "recover")
    public void recover(Long id) {
        int rows = applyInfoMapper.recoverById(id);
        if (rows == 0) {
            throw new BusinessException("申请记录不存在");
        }
    }

    @Override
    @OperationLog(value = "更新申请", type = "update")
    public void update(Long id, ApplyDto dto) {
        ApplyInfo apply = applyInfoMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }
        apply.setProductId(dto.getProductId());
        apply.setRealName(dto.getRealName());
        apply.setPhone(dto.getPhone());
        apply.setApplyType(dto.getApplyType());
        applyInfoMapper.updateById(apply);
    }

    @Override
    @OperationLog(value = "删除申请", type = "delete")
    public void delete(Long id) {
        ApplyInfo apply = applyInfoMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }
        applyInfoMapper.deleteById(id); // MyBatis-Plus 逻辑删除
    }

    @Override
    @OperationLog(value = "审批申请", type = "approve")
    public void approve(Long id, Integer status, String remark, Long approverId) {
        ApplyInfo apply = applyInfoMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }
        apply.setStatus(status);
        apply.setRemark(remark != null ? remark : "");
        apply.setApproverId(approverId);
        apply.setApproveTime(LocalDateTime.now());
        applyInfoMapper.updateById(apply);

        // 审批通过写入交易流水 + 贷款放款
        if (status == 1) {
            TransactionRecord tr = new TransactionRecord();
            tr.setUserId(apply.getUserId()); tr.setFee(java.math.BigDecimal.ZERO); tr.setStatus(1); tr.setTradeTime(LocalDateTime.now());
            if (apply.getApplyType() == 1) { tr.setType(1); tr.setAmount(java.math.BigDecimal.ZERO); tr.setRemark("办卡激活"); }
            else {
                java.math.BigDecimal loanAmt = new java.math.BigDecimal("50000");
                tr.setType(4); tr.setAmount(loanAmt); tr.setRemark("贷款放款");
                if (apply.getCardId() != null) {
                    BankCard targetCard = bankCardMapper.selectById(apply.getCardId());
                    if (targetCard != null && targetCard.getUserId().equals(apply.getUserId())) {
                        targetCard.setBalance(targetCard.getBalance().add(loanAmt));
                        bankCardMapper.updateById(targetCard);
                        tr.setToAccount(targetCard.getCardNumber());
                    }
                }
            }
            transactionMapper.insert(tr);
        }

        // 办卡审批通过 → 根据产品判断卡类型
        if (status == 1 && apply.getApplyType() == 1) {
            BankProduct product = bankProductMapper.selectById(apply.getProductId());
            int cardType = 2; // 默认信用卡
            if (product != null) {
                String name = product.getProductName();
                if (name.contains("借记")) cardType = 1;  // 借记卡
                else if (name.contains("储蓄")) cardType = 1;
            }
            bankCardService.create(apply.getUserId(), apply.getId(), cardType);
        }

        // 发送审核结果通知
        Message msg = new Message();
        msg.setUserId(apply.getUserId());
        msg.setTitle(status == 1 ? "申请已通过" : "申请已驳回");
        msg.setContent("您的" + (apply.getApplyType() == 1 ? "办卡" : "贷款") + "申请已"
            + (status == 1 ? "通过" : "驳回") + (remark != null && !remark.isEmpty() ? "，原因：" + remark : ""));
        msg.setType("approval");
        msg.setIsRead(0);
        messageMapper.insert(msg);
    }

    @Override
    @OperationLog(value = "彻底删除申请", type = "wipe")
    public void wipe(Long id) {
        applyInfoMapper.wipeById(id);
    }
}

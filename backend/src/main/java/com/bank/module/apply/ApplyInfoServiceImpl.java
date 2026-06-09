package com.bank.module.apply;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.log.OperationLog;
import com.bank.module.apply.dto.ApplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 申请服务实现
 */
@Service
@RequiredArgsConstructor
public class ApplyInfoServiceImpl implements ApplyInfoService {

    private final ApplyInfoMapper applyInfoMapper;

    @Override
    @OperationLog(value = "提交申请", type = "add")
    public void submit(ApplyDto dto, Long userId) {
        ApplyInfo apply = new ApplyInfo();
        apply.setUserId(userId);
        apply.setProductId(dto.getProductId());
        apply.setRealName(dto.getRealName());
        apply.setPhone(dto.getPhone());
        apply.setApplyType(dto.getApplyType());
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
    @OperationLog(value = "彻底删除申请", type = "wipe")
    public void wipe(Long id) {
        applyInfoMapper.wipeById(id);
    }
}

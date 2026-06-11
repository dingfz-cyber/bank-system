package com.bank.module.apply;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bank.module.apply.dto.ApplyDto;

/**
 * 申请服务接口
 */
public interface ApplyInfoService {

    /**
     * 提交申请
     */
    void submit(ApplyDto dto, Long userId);

    /**
     * 分页查询申请记录
     */
    IPage<ApplyInfo> page(long page, long pageSize, Long userId);

    /**
     * 回收站查询
     */
    IPage<ApplyInfo> recyclePage(long page, long pageSize);

    /**
     * 恢复申请
     */
    void recover(Long id);

    /**
     * 更新申请
     */
    void update(Long id, ApplyDto dto);

    /**
     * 软删除申请
     */
    void delete(Long id);

    /**
     * 审批申请
     */
    void approve(Long id, Integer status, String remark, Long approverId);

    /**
     * 物理删除
     */
    void wipe(Long id);
}

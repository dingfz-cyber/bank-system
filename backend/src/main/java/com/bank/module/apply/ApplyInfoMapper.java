package com.bank.module.apply;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 申请 Mapper
 */
@Mapper
public interface ApplyInfoMapper extends BaseMapper<ApplyInfo> {

    /** 物理删除（绕过逻辑删除） */
    void wipeById(Long id);

    /** 恢复申请（绕过逻辑删除） */
    int recoverById(Long id);

    /** 查询已删除申请列表 */
    List<ApplyInfo> selectDeletedList();

    /** 查询已删除总数 */
    long selectDeletedCount();
}

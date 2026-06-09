package com.bank.module.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品 Mapper
 */
@Mapper
public interface BankProductMapper extends BaseMapper<BankProduct> {

    /** 物理删除（绕过逻辑删除） */
    void wipeById(Long id);

    /** 恢复产品（绕过逻辑删除） */
    int recoverById(Long id);

    /** 查询已删除产品列表 */
    List<BankProduct> selectDeletedList(@Param("productType") Integer productType);

    /** 查询已删除总数 */
    long selectDeletedCount(@Param("productType") Integer productType);
}

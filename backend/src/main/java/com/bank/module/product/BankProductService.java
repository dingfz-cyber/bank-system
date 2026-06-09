package com.bank.module.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bank.module.product.dto.ProductQueryDto;

/**
 * 产品服务接口
 */
public interface BankProductService {

    /**
     * 分页查询产品
     */
    IPage<BankProduct> page(ProductQueryDto dto);

    /**
     * 产品详情
     */
    BankProduct detail(Long id);

    /**
     * 新增产品
     */
    void add(BankProduct product);

    /**
     * 修改产品
     */
    void update(BankProduct product);

    /**
     * 软删除产品
     */
    void delete(Long id);

    /**
     * 回收站查询
     */
    IPage<BankProduct> recyclePage(ProductQueryDto dto);

    /**
     * 恢复产品
     */
    void recover(Long id);

    /**
     * 物理删除
     */
    void wipe(Long id);
}

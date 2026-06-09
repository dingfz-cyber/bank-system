package com.bank.module.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.log.OperationLog;
import com.bank.module.product.dto.ProductQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品服务实现
 */
@Service
@RequiredArgsConstructor
public class BankProductServiceImpl implements BankProductService {

    private final BankProductMapper bankProductMapper;

    @Override
    public IPage<BankProduct> page(ProductQueryDto dto) {
        LambdaQueryWrapper<BankProduct> wrapper = new LambdaQueryWrapper<>();
        if (dto.getProductType() != null && dto.getProductType() > 0) {
            wrapper.eq(BankProduct::getProductType, dto.getProductType());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(BankProduct::getProductName, dto.getKeyword());
        }
        wrapper.orderByAsc(BankProduct::getSort);
        return bankProductMapper.selectPage(new Page<>(dto.getPage(), dto.getPageSize()), wrapper);
    }

    @Override
    public BankProduct detail(Long id) {
        BankProduct product = bankProductMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        return product;
    }

    @Override
    @OperationLog(value = "新增产品", type = "add")
    public void add(BankProduct product) {
        bankProductMapper.insert(product);
    }

    @Override
    @OperationLog(value = "修改产品", type = "update")
    public void update(BankProduct product) {
        BankProduct existing = bankProductMapper.selectById(product.getId());
        if (existing == null) {
            throw new BusinessException("产品不存在");
        }
        bankProductMapper.updateById(product);
    }

    @Override
    @OperationLog(value = "删除产品", type = "delete")
    public void delete(Long id) {
        BankProduct existing = bankProductMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("产品不存在");
        }
        bankProductMapper.deleteById(id); // MyBatis-Plus 逻辑删除
    }

    @Override
    public IPage<BankProduct> recyclePage(ProductQueryDto dto) {
        // 使用自定义 SQL 绕过 MP 逻辑删除过滤
        List<BankProduct> list = bankProductMapper.selectDeletedList(dto.getProductType());
        long total = bankProductMapper.selectDeletedCount(dto.getProductType());

        Page<BankProduct> page = new Page<>(dto.getPage(), dto.getPageSize(), total);
        // 手动分页
        int fromIndex = (int) ((dto.getPage() - 1) * dto.getPageSize());
        int toIndex = Math.min(fromIndex + (int) dto.getPageSize(), list.size());
        if (fromIndex < list.size()) {
            page.setRecords(list.subList(fromIndex, toIndex));
        } else {
            page.setRecords(List.of());
        }
        return page;
    }

    @Override
    @OperationLog(value = "恢复产品", type = "recover")
    public void recover(Long id) {
        int rows = bankProductMapper.recoverById(id);
        if (rows == 0) {
            throw new BusinessException("产品不存在");
        }
    }

    @Override
    @OperationLog(value = "彻底删除产品", type = "wipe")
    public void wipe(Long id) {
        bankProductMapper.wipeById(id);
    }
}

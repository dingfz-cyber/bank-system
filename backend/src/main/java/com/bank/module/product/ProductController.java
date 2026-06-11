package com.bank.module.product;

import com.bank.common.PageResult;
import com.bank.common.Result;
import com.bank.module.product.dto.ProductQueryDto;
import com.bank.rbac.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 产品控制器
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final BankProductService bankProductService;

    /**
     * 分页查询产品列表（公开）
     */
    @GetMapping("/list")
    public Result<PageResult<BankProduct>> list(@Valid ProductQueryDto dto) {
        return Result.success(PageResult.of(bankProductService.page(dto)));
    }

    /**
     * 产品详情
     */
    @GetMapping("/detail/{id}")
    public Result<BankProduct> detail(@PathVariable Long id) {
        return Result.success(bankProductService.detail(id));
    }

    /**
     * 新增产品（管理员）
     */
    @PostMapping("/add")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> add(@Valid @RequestBody BankProduct product) {
        bankProductService.add(product);
        return Result.success();
    }

    /**
     * 修改产品（管理员）
     */
    @PutMapping("/update")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> update(@Valid @RequestBody BankProduct product) {
        bankProductService.update(product);
        return Result.success();
    }

    /**
     * 软删除产品（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> delete(@PathVariable Long id) {
        bankProductService.delete(id);
        return Result.success();
    }

    /**
     * 回收站查询（管理员）
     */
    @GetMapping("/recycle")
    @RequireRole("BIZ_ADMIN")
    public Result<PageResult<BankProduct>> recycle(@Valid ProductQueryDto dto) {
        return Result.success(PageResult.of(bankProductService.recyclePage(dto)));
    }

    /**
     * 恢复产品（管理员）
     */
    @PostMapping("/recover/{id}")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> recover(@PathVariable Long id) {
        bankProductService.recover(id);
        return Result.success();
    }

    /**
     * 物理删除（管理员）
     */
    @DeleteMapping("/wipe/{id}")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> wipe(@PathVariable Long id) {
        bankProductService.wipe(id);
        return Result.success();
    }
}

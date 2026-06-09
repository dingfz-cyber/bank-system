package com.bank.module.banner;

import com.bank.common.Result;
import com.bank.rbac.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播控制器
 */
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 轮播列表（公开）
     */
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.success(bannerService.list());
    }

    /**
     * 新增轮播（管理员）
     */
    @PostMapping("/add")
    @RequireRole("SUPER_ADMIN")
    public Result<Void> add(@Valid @RequestBody Banner banner) {
        bannerService.add(banner);
        return Result.success();
    }

    /**
     * 修改轮播（管理员）
     */
    @PutMapping("/update")
    @RequireRole("SUPER_ADMIN")
    public Result<Void> update(@Valid @RequestBody Banner banner) {
        bannerService.update(banner);
        return Result.success();
    }

    /**
     * 删除轮播（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @RequireRole("SUPER_ADMIN")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return Result.success();
    }
}

package com.bank.module.apply;

import com.bank.common.NotLoginException;
import com.bank.common.PageResult;
import com.bank.common.Result;
import com.bank.module.apply.dto.ApplyDto;
import com.bank.rbac.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 申请控制器
 */
@RestController
@RequestMapping("/apply")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyInfoService applyInfoService;

    /**
     * 从请求中获取当前用户 ID
     */
    private Long getUserId(HttpServletRequest request) {
        Object attr = request.getAttribute("userId");
        if (attr == null) throw new NotLoginException("未登录");
        return Long.parseLong(attr.toString());
    }

    /**
     * 从请求中获取当前角色 ID
     */
    private Long getRoleId(HttpServletRequest request) {
        Object attr = request.getAttribute("roleId");
        if (attr == null) throw new NotLoginException("未登录");
        return Long.parseLong(attr.toString());
    }

    /**
     * 提交申请（需登录）
     */
    @PostMapping("/submit")
    public Result<Void> submit(@Valid @RequestBody ApplyDto dto, HttpServletRequest request) {
        Long userId = getUserId(request);
        applyInfoService.submit(dto, userId);
        return Result.success();
    }

    /**
     * 分页查询申请记录
     * 管理员看全部，普通用户只看自己的
     */
    @GetMapping("/list")
    public Result<PageResult<ApplyInfo>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        Long roleId = getRoleId(request);
        Long userId = null;
        // 非管理员只看自己的申请
        if (roleId != 1L && roleId != 2L) {
            userId = getUserId(request);
        }
        return Result.success(PageResult.of(applyInfoService.page(page, pageSize, userId)));
    }

    /**
     * 回收站查询（管理员）
     */
    @GetMapping("/recycle")
    @RequireRole("SUPER_ADMIN")
    public Result<PageResult<ApplyInfo>> recycle(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(PageResult.of(applyInfoService.recyclePage(page, pageSize)));
    }

    /**
     * 恢复申请（管理员）
     */
    @PostMapping("/recover/{id}")
    @RequireRole("SUPER_ADMIN")
    public Result<Void> recover(@PathVariable Long id) {
        applyInfoService.recover(id);
        return Result.success();
    }

    /**
     * 物理删除（管理员）
     */
    @DeleteMapping("/wipe/{id}")
    @RequireRole("SUPER_ADMIN")
    public Result<Void> wipe(@PathVariable Long id) {
        applyInfoService.wipe(id);
        return Result.success();
    }
}

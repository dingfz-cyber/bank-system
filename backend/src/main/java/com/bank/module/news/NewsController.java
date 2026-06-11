package com.bank.module.news;

import com.bank.common.PageResult;
import com.bank.common.Result;
import com.bank.module.news.dto.NewsQueryDto;
import com.bank.rbac.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻公告控制器
 */
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * 分页查询新闻（公开，仅已发布）
     */
    @GetMapping("/list")
    public Result<PageResult<News>> list(@Valid NewsQueryDto dto) {
        return Result.success(PageResult.of(newsService.page(dto)));
    }

    /**
     * 新闻详情（公开）
     */
    @GetMapping("/detail/{id}")
    public Result<News> detail(@PathVariable Long id) {
        return Result.success(newsService.detail(id));
    }

    /**
     * 管理端分页查询（管理员，含草稿）
     */
    @GetMapping("/admin/list")
    @RequireRole("BIZ_ADMIN")
    public Result<PageResult<News>> adminList(@Valid NewsQueryDto dto) {
        return Result.success(PageResult.of(newsService.adminPage(dto)));
    }

    /**
     * 新增新闻（管理员）
     */
    @PostMapping("/add")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> add(@Valid @RequestBody News news) {
        newsService.add(news);
        return Result.success();
    }

    /**
     * 修改新闻（管理员）
     */
    @PutMapping("/update")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> update(@Valid @RequestBody News news) {
        newsService.update(news);
        return Result.success();
    }

    /**
     * 软删除新闻（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return Result.success();
    }
}

package com.bank.module.news;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bank.module.news.dto.NewsQueryDto;

/**
 * 新闻公告服务接口
 */
public interface NewsService {

    /**
     * 分页查询新闻（公开，仅已发布）
     */
    IPage<News> page(NewsQueryDto dto);

    /**
     * 管理端分页查询（含草稿）
     */
    IPage<News> adminPage(NewsQueryDto dto);

    /**
     * 新闻详情
     */
    News detail(Long id);

    /**
     * 新增新闻
     */
    void add(News news);

    /**
     * 修改新闻
     */
    void update(News news);

    /**
     * 软删除
     */
    void delete(Long id);
}

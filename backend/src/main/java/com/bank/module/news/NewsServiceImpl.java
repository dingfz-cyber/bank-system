package com.bank.module.news;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.log.OperationLog;
import com.bank.module.news.dto.NewsQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 新闻公告服务实现
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;

    @Override
    public IPage<News> page(NewsQueryDto dto) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getStatus, 1); // 仅已发布
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(News::getTitle, dto.getKeyword());
        }
        wrapper.orderByAsc(News::getSort).orderByDesc(News::getCreateTime);
        return newsMapper.selectPage(new Page<>(dto.getPage(), dto.getPageSize()), wrapper);
    }

    @Override
    public IPage<News> adminPage(NewsQueryDto dto) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(News::getTitle, dto.getKeyword());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(News::getStatus, dto.getStatus());
        }
        wrapper.orderByAsc(News::getSort).orderByDesc(News::getCreateTime);
        return newsMapper.selectPage(new Page<>(dto.getPage(), dto.getPageSize()), wrapper);
    }

    @Override
    public News detail(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }
        return news;
    }

    @Override
    @OperationLog(value = "新增新闻", type = "add")
    public void add(News news) {
        newsMapper.insert(news);
    }

    @Override
    @OperationLog(value = "修改新闻", type = "update")
    public void update(News news) {
        News existing = newsMapper.selectById(news.getId());
        if (existing == null) {
            throw new BusinessException("新闻不存在");
        }
        newsMapper.updateById(news);
    }

    @Override
    @OperationLog(value = "删除新闻", type = "delete")
    public void delete(Long id) {
        News existing = newsMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("新闻不存在");
        }
        newsMapper.deleteById(id);
    }
}

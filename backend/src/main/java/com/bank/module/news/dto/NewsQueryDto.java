package com.bank.module.news.dto;

import lombok.Data;

/**
 * 新闻查询 DTO
 */
@Data
public class NewsQueryDto {

    private long page = 1;

    private long pageSize = 10;

    /** 关键词（标题模糊搜索） */
    private String keyword;

    /** 状态筛选：0=草稿 1=已发布，null=全部 */
    private Integer status;
}

package com.bank.module.banner;

import java.util.List;

/**
 * 轮播服务接口
 */
public interface BannerService {

    /**
     * 查询所有轮播（按排序）
     */
    List<Banner> list();

    /**
     * 新增轮播
     */
    void add(Banner banner);

    /**
     * 修改轮播
     */
    void update(Banner banner);

    /**
     * 软删除轮播
     */
    void delete(Long id);
}

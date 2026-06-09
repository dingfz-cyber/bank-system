package com.bank.module.banner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.common.BusinessException;
import com.bank.log.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播服务实现
 */
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public List<Banner> list() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Banner::getSort);
        return bannerMapper.selectList(wrapper);
    }

    @Override
    @OperationLog(value = "新增轮播", type = "add")
    public void add(Banner banner) {
        bannerMapper.insert(banner);
    }

    @Override
    @OperationLog(value = "修改轮播", type = "update")
    public void update(Banner banner) {
        Banner existing = bannerMapper.selectById(banner.getId());
        if (existing == null) {
            throw new BusinessException("轮播不存在");
        }
        bannerMapper.updateById(banner);
    }

    @Override
    @OperationLog(value = "删除轮播", type = "delete")
    public void delete(Long id) {
        Banner existing = bannerMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("轮播不存在");
        }
        bannerMapper.deleteById(id);
    }
}

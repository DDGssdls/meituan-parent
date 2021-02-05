package com.ddg.meituan.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.common.utils.Query;

import com.ddg.meituan.product.dao.BrandDao;
import com.ddg.meituan.product.entity.BrandEntity;
import com.ddg.meituan.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    private final BrandDao brandDao;

    @Autowired
    public BrandServiceImpl(BrandDao brandDao) {
        this.brandDao = brandDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void updateStatus(BrandEntity brandEntity) {
        brandDao.updateById(brandEntity);
    }

    @Override
    public List<BrandEntity> getByBrandIds(List<Long> ids) {
        return brandDao.selectBatchIds(ids);
    }

}
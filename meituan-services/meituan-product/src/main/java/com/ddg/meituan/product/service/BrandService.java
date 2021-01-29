package com.ddg.meituan.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author 
 * @email 
 * @date 2021-01-29 11:22:05
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


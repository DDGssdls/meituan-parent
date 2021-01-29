package com.ddg.meituan.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author 
 * @email 
 * @date 2021-01-29 11:22:05
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


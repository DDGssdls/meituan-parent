package com.ddg.meituan.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author 
 * @email 
 * @date 2021-01-29 11:22:05
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


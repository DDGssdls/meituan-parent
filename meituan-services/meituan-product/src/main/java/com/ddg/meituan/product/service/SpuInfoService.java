package com.ddg.meituan.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author 
 * @email 
 * @date 2021-01-29 11:22:05
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


package com.ddg.meituan.product.dao;

import com.ddg.meituan.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author 
 * @email 
 * @date 2021-01-29 11:22:05
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

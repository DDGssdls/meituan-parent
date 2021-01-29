package com.ddg.meituan.product.service.impl;


import com.ddg.meituan.common.annotation.RedisCache;
import com.ddg.meituan.product.constant.ProductConstant;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.common.utils.Query;

import com.ddg.meituan.product.dao.CategoryDao;
import com.ddg.meituan.product.entity.CategoryEntity;
import com.ddg.meituan.product.service.CategoryService;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @RedisCache(redisKey = "redis_list_tree", resultObjClass = "com.ddg.meituan.product.entity.CategoryEntity",
            isList = true)
    public List<CategoryEntity> getListWithTree() {
        //首先是获取所有的CategoryEntity
        List<CategoryEntity> list = categoryDao.selectList(null);
        // 进行父子结构的组装：
        List<CategoryEntity> fatherList = list.stream()
                .map(categoryEntity -> setChildren(categoryEntity, list))
                .filter(categoryEntity -> ProductConstant.CAT_LEVEL.equals(categoryEntity.getCatLevel()) )
                .sorted(Comparator.comparingInt(CategoryEntity::getSort)).limit(ProductConstant.MAX_FATHER_LENGTH).collect(Collectors.toList());
        return fatherList;
    }
    // 进行父子结构的组装：每一个分类下最多返回15个子节点
    private CategoryEntity setChildren(CategoryEntity categoryEntity, List<CategoryEntity> list) {
        List<CategoryEntity> collect = list.stream()
                .filter(categoryEntity1 -> categoryEntity.getCatId().equals(categoryEntity1.getParentCid()))
                .limit(ProductConstant.MAX_COUNT).collect(Collectors.toList());
        categoryEntity.setChildren(collect);
        return categoryEntity;

    }

}
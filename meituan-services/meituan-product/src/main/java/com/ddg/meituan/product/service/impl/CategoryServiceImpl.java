package com.ddg.meituan.product.service.impl;


import com.ddg.meituan.common.annotation.RedisCache;
import com.ddg.meituan.common.annotation.RemoveCache;
import com.ddg.meituan.product.constant.ProductConstant;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.common.utils.Query;

import com.ddg.meituan.product.dao.CategoryDao;
import com.ddg.meituan.product.entity.CategoryEntity;
import com.ddg.meituan.product.service.CategoryService;
import org.springframework.util.StringUtils;

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
        // 获取所有的二级分类：
        List<CategoryEntity> categoryEntityList = list.stream()
                .filter(categoryEntity -> ProductConstant.CAT_LEVEL_TWO.equals(categoryEntity.getCatLevel())).collect(Collectors.toList());
        // 进行父子结构的组装：每一个分类下最多返回15个子节点
        List<CategoryEntity> fatherList = list.stream()
                .map(categoryEntity -> setChildren(categoryEntity, list, ProductConstant.MAX_LEVEL3_COUNT))
                .filter(categoryEntity -> ProductConstant.CAT_LEVEL_ONE.equals(categoryEntity.getCatLevel()) )
                .sorted(Comparator.comparingInt(CategoryEntity::getSort)).limit(ProductConstant.MAX_FATHER_LENGTH).collect(Collectors.toList());
        return fatherList;
    }

    @Override
    @RemoveCache("redis_list_tree")
    public void changeStatus(CategoryEntity categoryEntity) {
        // 返回的直接是修改之后的显示状态 直接进行数据库的更新即可
        categoryDao.updateById(categoryEntity);
    }

    @Override
    public PageUtils queryPageById(Map<String, Object> params) {
        String cartIdStr = (String) params.get(ProductConstant.CART_ID);
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(cartIdStr)){
            Long cartId = Long.parseLong(cartIdStr);
            wrapper.eq(ProductConstant.PARENT_CART_ID, cartId);
        }
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> getParentList() {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.ne(ProductConstant.CART_LEVEL, ProductConstant.CAT_LEVEL_THREE);
        List<CategoryEntity> list = categoryDao.selectList(wrapper);
        List<CategoryEntity> collect = list.stream().map(categoryEntity -> setChildren(categoryEntity, list,
                ProductConstant.MAX_LEVEL2_COUNT))
                .filter(categoryEntity -> ProductConstant.CAT_LEVEL_ONE.equals(categoryEntity.getCatLevel()) )
                .sorted(Comparator.comparingInt(CategoryEntity::getSort)).limit(ProductConstant.MAX_FATHER_LENGTH).collect(Collectors.toList());
        return collect;
    }

    // 进行父子结构的组装：
    private CategoryEntity setChildren(CategoryEntity categoryEntity, List<CategoryEntity> list, Integer limit) {
        List<CategoryEntity> collect = list.stream()
                .filter(categoryEntity1 -> categoryEntity.getCatId().equals(categoryEntity1.getParentCid()))
                .limit(limit).collect(Collectors.toList());
        categoryEntity.setChildren(collect);
        return categoryEntity;

    }

}
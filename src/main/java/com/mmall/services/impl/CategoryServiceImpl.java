package com.mmall.services.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.services.ICategoryService;
import com.mmall.vo.CategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse addCategory(String categoryName,Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("商品分类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int resulte = categoryMapper.insertSelective(category);
        if (resulte > 0 ){
            return ServerResponse.createBySuccess("商品分类添加成功");
        }
        return ServerResponse.createByErrorMessage("商品分类添加失败");
    }

    public ServerResponse setCategory(Integer categoryId,String categoryName){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("商品分类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int resulte = categoryMapper.updateByPrimaryKeySelective(category);
        if (resulte > 0 ){
            return ServerResponse.createBySuccess("商品分类修改成功");
        }
        return ServerResponse.createByErrorMessage("商品分类修改失败");
    }

    public ServerResponse<List<Category>> getNextChildCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectNextChildCategory(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到该分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse<List<CategoryVo>> selectDeepCategory(Integer categoryId){
        Set<Category> categorySet = Sets.newLinkedHashSet();
        findChildCategory(categorySet,categoryId);
        List<CategoryVo> categoryList = Lists.newArrayList();
        if(categoryId != null){
            categoryList = setCategoryVo(categorySet);
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){

        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        //mybatis 对集合的返回不会返回null
        List<Category> categoryList = categoryMapper.selectNextChildCategory(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
            return categorySet;
    }

    private List<CategoryVo> setCategoryVo (Set<Category> categorySet){
        List<CategoryVo> categoryVoList = Lists.newArrayList();
        for(Category categoryItem : categorySet ){
            CategoryVo categoryVo = new CategoryVo();
            categoryVo.setId(categoryItem.getId());
            categoryVo.setName(categoryItem.getName());
            categoryVo.setParentId(categoryItem.getParentId());
            categoryVo.setStatus(categoryItem.getStatus());
            categoryVoList.add(categoryVo);
        }
        return categoryVoList;
    }
}

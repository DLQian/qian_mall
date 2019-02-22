package com.mmall.services;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.vo.CategoryVo;

import java.util.List;

public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse setCategory(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getNextChildCategory(Integer categoryId);

    ServerResponse<List<CategoryVo>> selectDeepCategory(Integer categoryId);

}

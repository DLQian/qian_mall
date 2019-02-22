package com.mmall.services;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse saveOrUpdate(Product product);

    ServerResponse setStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> backProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    ServerResponse backSearchProduct(String productName , Integer productId , Integer pageNum,Integer pageSize);

    ServerResponse<ProductDetailVo> productDetail(Integer productId);

    ServerResponse<PageInfo> searchProduct(String productName,Integer categoryID,Integer pageNum,Integer pageSize,String orderBy);

    ServerResponse inputImage(Integer productId , String image);

}

package com.mmall.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.services.ICategoryService;
import com.mmall.services.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CategoryVo;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveOrUpdate(Product product){
        if (product == null){
            return ServerResponse.createByErrorMessage("商品参数错误");
        }
        if (product.getId() == null){
            int result = productMapper.insertSelective(product);
            if (result > 0 ){
                return ServerResponse.createBySuccessMessage("商品添加成功");
            }
            return ServerResponse.createByErrorMessage("商品添加失败");
        }
       else{
            int result = productMapper.updateByPrimaryKeySelective(product);
            if (result > 0 ){
                return ServerResponse.createBySuccessMessage("商品更新成功");
            }
            return ServerResponse.createByErrorMessage("商品更新失败");
        }
    }

    public ServerResponse setStatus(Integer productId,Integer status){
        if (productId == null || status == null){
            return ServerResponse.createByErrorMessage("商品Id或商品状态参数错误");
        }
        Product setProduct = new Product();
        setProduct.setId(productId);
        setProduct.setStatus(status);
        int result = productMapper.updateByPrimaryKeySelective(setProduct);
        if (result > 0){
            return ServerResponse.createBySuccessMessage("商品状态更改成功");
        }
        return ServerResponse.createByErrorMessage("商品状态更改失败");
    }

    public ServerResponse<ProductDetailVo> backProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorMessage("商品Id参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("商品已经被下架或商品不存在");
        }
        ProductDetailVo productDetailVo = setProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo setProductDetailVo (Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.qmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectProductList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = setProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo result = new PageInfo(productList);
        result.setList(productListVoList);
        return ServerResponse.createBySuccess(result);
    }

    private ProductListVo setProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.qmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse backSearchProduct(String productName , Integer productId , Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);

        String searchName = null;
        if (!StringUtils.isBlank(productName)){
            searchName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectProductByProductNameOrProductId(searchName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = setProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo result = new PageInfo(productList);
        result.setList(productListVoList);
        return ServerResponse.createBySuccess(result);
    }

    public ServerResponse<ProductDetailVo> productDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorMessage("商品Id参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("商品已经被下架或商品不存在");
        }
        if (product.getStatus() != Const.Status.STATUS_SOLD)
            return ServerResponse.createByErrorMessage("商品已经被下架或商品不存在");
        ProductDetailVo productDetailVo = setProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy) {
        if ( StringUtils.isBlank(productName) && categoryId == null ) {
            return ServerResponse.createByErrorMessage("商品搜索参数错误");
        }
        List<CategoryVo> categoryList = new ArrayList<CategoryVo>();
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(productName)) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryList = iCategoryService.selectDeepCategory(categoryId).getData();
            for (CategoryVo myCategoty : categoryList){
                categoryIdList.add(myCategoty.getId());
            }
        }
        String searchName = null;
        if (StringUtils.isNotBlank(productName)){
            searchName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(orderBy)) {
            if(Const.ProductOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByRule = orderBy.split("_");
                PageHelper.orderBy(orderByRule[0] + " " + orderByRule[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIdList(searchName,categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            productListVoList.add(setProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse inputImage(Integer productId , String image){
        if (image == null ){
            return ServerResponse.createByErrorMessage("图片不能为空");
        }else{
            int result = productMapper.updateImageByProductId(productId,image);
            return ServerResponse.createBySuccess("图片更新成功");
        }
    }
}

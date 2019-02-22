package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.services.ICategoryService;
import com.mmall.services.IProductService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 前台展示商品详情
     * @param productId
     * @return9
     */
    @RequestMapping("product_detail.do")
    @ResponseBody
    public ModelAndView productDetail(HttpSession session ,Integer productId){
        ModelAndView result = new ModelAndView("pro/product/detailproduct");
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        result.addObject("data",user);
        ServerResponse product = iProductService.productDetail(productId);
        result.addObject("product",product.getData());
        return result;
    }

    /**
     * 搜索商品
     * @param productName
     * @param categoryId
     * @param orderBy
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search_product.do")
    @ResponseBody
    public ServerResponse searchProduct(@RequestParam(value = "productName",required = false)String productName,
                                        @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                        @RequestParam(value = "orderBy",required = false)String orderBy,
                                        @RequestParam(value = "pageNum" ,defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize" ,defaultValue = "10")Integer pageSize){
        return iProductService.searchProduct(productName,categoryId,pageNum,pageSize,orderBy);
    }

    /**
     * 跳转商品页面
     * @param session
     * @return
     */
    @RequestMapping("to_product.do")
    @ResponseBody
    public ModelAndView productList(HttpSession session,@RequestParam(value = "choose",defaultValue = "100001")int choose){
        ModelAndView result = new ModelAndView("pro/product/allproduct");
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iCategoryService.getNextChildCategory(0);
        result.addObject("data",user);
        result.addObject("category",response);
        result.addObject("choose",choose);
        ServerResponse product = iProductService.searchProduct("",choose,1,10,"");
        result.addObject("product",product);

        return result;
    }
}

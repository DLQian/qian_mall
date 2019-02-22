package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.services.IAddressService;
import com.mmall.services.ICartService;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
@Controller
@RequestMapping("cart")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @Autowired
    private IAddressService iAddressService;

    /**
     * 添加购物车商品
     * @param session
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ModelAndView add(HttpSession session,Integer productId,Integer count){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            ModelAndView result = new ModelAndView("result/profail");
            ServerResponse data =ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
            result.addObject("data",data);
            return result;
        }
        ServerResponse response = iCartService.add(user.getId(),productId,count);
        if (response.getStatus() == 0){
            ModelAndView result = new ModelAndView("result/prosuccess");
            return result;
        }else{
            ModelAndView result = new ModelAndView("result/profail");
            return result;
        }
    }

    /**
     * 更新购物车商品数量
     * @param session
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody

    public ServerResponse update(HttpSession session,Integer productId,Integer count){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iCartService.update(user.getId(),productId,count);
    }

    /**
     * 删除购物车商品
     * @param session
     * @param productIds
     * @return
     */
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session,String productIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iCartService.delete(user.getId(),productIds);
    }

    /**
     * 查询购物车商品
     * @param session
     * @return
     */
    @RequestMapping("find.do")
    @ResponseBody
    public ModelAndView find(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            ModelAndView fail = new ModelAndView("result/profail");
            ServerResponse response =  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
            fail.addObject("data",response);
            return fail;
        }
        ModelAndView success = new ModelAndView("pro/cart/cart");
        ServerResponse cart = iCartService.find(user.getId());
        ServerResponse data = ServerResponse.createBySuccess(user);
        ServerResponse address = iAddressService.list(user.getId());
        success.addObject("data",data);
        success.addObject("cart",cart);
        success.addObject("address",address);
        return  success;
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping("check_all.do")
    @ResponseBody
    public ServerResponse checkAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iCartService.checkToSelectOrUnselect(user.getId(),null,Const.Cart.CHECK_SELECT);
    }

    /**
     * 全不选
     * @param session
     * @return
     */
    @RequestMapping("uncheck_all.do")
    @ResponseBody
    public ServerResponse uncheckAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iCartService.checkToSelectOrUnselect(user.getId(),null,Const.Cart.CHECK_UNSELECT);
    }

    /**
     * 单不选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("uncheck.do")
    @ResponseBody
    public ServerResponse uncheckOne(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iCartService.checkToSelectOrUnselect(user.getId(),productId,Const.Cart.CHECK_UNSELECT);
    }

    /**
     * 单选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("check.do")
    @ResponseBody
    public ServerResponse checkOne(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iCartService.checkToSelectOrUnselect(user.getId(),productId,Const.Cart.CHECK_SELECT);
    }

    @RequestMapping("cart_product_count.do")
    @ResponseBody
    public ServerResponse cartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iCartService.getCartProudctCount(user.getId());
    }

}

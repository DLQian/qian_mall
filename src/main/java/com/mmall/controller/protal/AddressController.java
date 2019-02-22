package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.services.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/address/")
public class AddressController {
    @Autowired
    private IAddressService iAddressService;

    /**
     * 添加地址
     * @param session
     * @param address
     * @return
     */
    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse add(HttpSession session , Shipping address){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iAddressService.add(user.getId(),address);
    }

    /**
     * 删除地址
     * @param session
     * @param addressId
     * @return
     */
    @RequestMapping(value = "delete.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete(HttpSession session , Integer addressId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iAddressService.delete(user.getId(),addressId);
    }

    /**
     * 更新地址
     * @param session
     * @param address
     * @return
     */
    @RequestMapping(value ="update.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update(HttpSession session , Shipping address){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iAddressService.update(user.getId(),address);
    }

    @RequestMapping(value ="find.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse find(HttpSession session , Integer addressId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iAddressService.find(user.getId(),addressId);
    }

    /**
     * 显示当前用户的全部地址
     * @param session
     * @return
     */
    @RequestMapping(value ="list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        return iAddressService.list(user.getId());
    }

}

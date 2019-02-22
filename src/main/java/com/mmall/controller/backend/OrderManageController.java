package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.services.IOrderService;
import com.mmall.services.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("admin/order")
public class OrderManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    /**
     * 显示订单列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list_order.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse listOder(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            return ServerResponse.createBySuccess(iOrderService.backListOrder(pageNum,pageSize));
        } else {
            return ServerResponse.createByErrorMessage("无权限进行操作");
        }
    }

    /**
     * 后台查看订单详情
     * @param session
     * @param orderNum
     * @return
     */
    @RequestMapping(value = "detail_order.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse detailOrder(HttpSession session , Long orderNum){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            return ServerResponse.createBySuccess(iOrderService.backDetailOrder(orderNum));
        } else {
            return ServerResponse.createByErrorMessage("无权限进行操作");
        }
    }

    /**
     * 后台查询订单
     * @param session
     * @param orderNum
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search_order.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse searchOrder(HttpSession session , Long orderNum,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            return ServerResponse.createBySuccess(iOrderService.backSearchOrder(orderNum,pageNum,pageSize));
        } else {
            return ServerResponse.createByErrorMessage("无权限进行操作");
        }
    }

    /**
     * 发货
     * @param session
     * @param orderNum
     * @return
     */
    @RequestMapping(value = "send.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse sendGoodsOrder(HttpSession session , Long orderNum){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            return ServerResponse.createBySuccess(iOrderService.backSendGoodsOrder(orderNum));
        } else {
            return ServerResponse.createByErrorMessage("无权限进行操作");
        }
    }


}

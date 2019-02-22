package com.mmall.controller.protal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.services.IAddressService;
import com.mmall.services.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderVo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.result.ModelResultMatchers;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IAddressService iAddressService;

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    /**
     * 支付
     * @param session
     * @param orderNum
     * @param request
     * @return
     */
    @RequestMapping(value = "pay.do",method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView pay(HttpSession session , Long orderNum , HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            ServerResponse response =  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
            ModelAndView fail = new ModelAndView("result/profail");
            fail.addObject("data",fail);
            return fail;
        }
        String path = PropertiesUtil.getProperty("picture.pay");
        ServerResponse pay = iOrderService.pay(user.getId(),orderNum,path);
        ServerResponse data = ServerResponse.createBySuccess(user);
        ModelAndView success = new ModelAndView("pro/order/pay");
        success.addObject("pay",pay);
        success.addObject("data",data);
        return success;
    }

    /**
     * 支付宝回调处理
     * @param request
     * @return
     */
    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){

        //获取回调信息
        Map requestMap = request.getParameterMap();
        //初始化自定义数组
        Map<String,String> myMap = Maps.newHashMap();
        //组装自定义数组
        for(Iterator iter = requestMap.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[])requestMap.get(name);
            String valueString = "";
            for (int i = 0 ; i < values.length ; i++){
                if (values.length == i+1){
                    valueString = valueString + values[i];
                } else
                    valueString = valueString + values[i] + ",";
            }
            myMap.put(name,valueString);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",myMap.get("sign"),myMap.get("trade_status"),myMap.toString());

        //验证支付宝回调的正确性
        //支付宝sdk 已经将sign设置为RSA2
        myMap.remove("sign_type");
        try {
            boolean alipayRSA2CheckV2 = AlipaySignature.rsaCheckV2(myMap, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if (!alipayRSA2CheckV2){
                return ServerResponse.createByErrorMessage("支付验证不通过，恶意攻击将记录IP提交司法处理");
            }
        } catch (AlipayApiException e) {
            log.error("支付回调错误",e);
        }

        ServerResponse serverResponse = iOrderService.alipay(myMap);
        if (serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;

        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单支付状态
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> orderPayStatus(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

    /**
     * 创建订单
     * @param session
     * @param addressId
     * @return
     */
    @RequestMapping(value = "create_order.do",method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView createOrder(HttpSession session, Integer addressId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            ServerResponse response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            ModelAndView fail = new ModelAndView("result/profail");
            fail.addObject("data",response);
            return fail;
        }
        ModelAndView success = new ModelAndView("pro/order/order");
        ServerResponse data = ServerResponse.createBySuccess(user);
        ServerResponse order = iOrderService.createOrder(user.getId(),addressId);
        ServerResponse address = iAddressService.find(user.getId(),addressId);
        success.addObject("data",data);
        success.addObject("order",order);
        success.addObject("address",address.getData());
        return success;
    }

    /**
     * 取消未支付的订单
     * @param session
     * @param orderNum
     * @return
     */
    @RequestMapping("cancel_order.do")
    @ResponseBody
    public ModelAndView cancelOrder(HttpSession session, Long orderNum){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            ServerResponse response =  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            ModelAndView fail = new ModelAndView("result/profail");
            fail.addObject("data",response);
            return fail;
        }
        iOrderService.cancelOrder(user.getId(),orderNum);
        ModelAndView success = new ModelAndView("pro/order/orderlist");
        ServerResponse order = iOrderService.listOrder(user.getId(),1,10);
        ServerResponse data = ServerResponse.createBySuccess(user);
        success.addObject("order",order);
        success.addObject("data",data);
        return success;
    }


    /**
     * 订单列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list_order.do")
    @ResponseBody
    public ModelAndView listOrder(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            ServerResponse response =  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            ModelAndView fail = new ModelAndView("result/profail");
            fail.addObject("data",response);
            return fail;

        }
        ModelAndView success = new ModelAndView("pro/order/orderlist");
        ServerResponse order = iOrderService.listOrder(user.getId(),pageNum,pageSize);
        ServerResponse data = ServerResponse.createBySuccess(user);
        success.addObject("order",order);
        success.addObject("data",data);
        return success;
    }

    /**
     * 查看订单详情
     * @param session
     * @param orderNum
     * @return
     */
    @RequestMapping("detail_order.do")
    @ResponseBody
    public ModelAndView detailOrder(HttpSession session, Long orderNum){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            ServerResponse response =  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            ModelAndView result = new ModelAndView("result/profail");
            result.addObject("data",response);
            return result;
        }
        ModelAndView success = new ModelAndView("pro/order/orderdetail");
        ServerResponse order =  iOrderService.detailOrder(user.getId(),orderNum);
        ServerResponse data = ServerResponse.createBySuccess(user);
        OrderVo add= (OrderVo)order.getData();
        ServerResponse address = iAddressService.find(user.getId(),add.getShippingId());
        success.addObject("order",order);
        success.addObject("data",data);
        success.addObject("address",address.getData());
        return success;
    }
}

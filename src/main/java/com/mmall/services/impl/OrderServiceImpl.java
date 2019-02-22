package com.mmall.services.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.services.FilerService;
import com.mmall.services.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileStore;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private FilerService filerService;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public ServerResponse pay(Integer userId,Long orderNum,String path){
        Map<String,String> resulteMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNum(userId,orderNum);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户不存在该订单号");
        }
        resulteMap.put("orderNum",String.valueOf(order.getOrderNo()));


        // 测试当面付2.0生成支付二维码

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("扫码支付 ： ").append(order.getOrderNo()).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "谦仔";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "闲时小饮";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemsList = orderItemMapper.selectByUserIdAndOrderNum(userId,orderNum);
        for (OrderItem orderItem : orderItemsList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(
                    orderItem.getProductId().toString(),
                    orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path,qrFileName);
//                try {
//                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
//                } catch (IOException e) {
//                    log.error("上传二维码异常",e);
//                }
                log.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("picture.pay")+targetFile.getName();
                resulteMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(resulteMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }

    }

    public ServerResponse alipay(Map<String,String> myMap){
        Long orderNum = Long.parseLong(myMap.get("out_trade_no"));
        String tradeNum = myMap.get("trade_no");
        String tradeStatus = myMap.get("trade_status");
        //判断订单是否存在
        Order order = orderMapper.selectByOrderNum(orderNum);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        //判断订单状态
        if (order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessMessage("订单已经完成交易状态，请勿重复回调");
        }
        if (Const.AlipayCallback.RESPONSE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(myMap.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        //组装payInfo信息
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(orderNum);
        payInfo.setUserId(order.getUserId());
        payInfo.setPlatformNumber(tradeNum);
        payInfo.setPlatformStatus(tradeStatus);
        payInfo.setPayPlatform(Const.PayWayEnum.ALIPAY.getCode());
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNum){
        Order order = orderMapper.selectByUserIdAndOrderNum(userId,orderNum);
        if(order == null){
            return ServerResponse.createByErrorMessage("用户不存在该订单号");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }


    public ServerResponse createOrder(Integer userId,Integer addressId){
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createBySuccessMessage("用户购物未添加商品");
        }
        //取出购物车商品
        ServerResponse serverResponse = this.getOrderItemList(userId,cartList);
        if (!serverResponse.isSuccess()){
            return  serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        //计算购物车总价
        BigDecimal totalPrice = this.getTotalPrice(orderItemList);
        //组装订单信息，生成订单
        Order order = this.setOrder(userId,addressId,totalPrice);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单生存失败");
        }
        //添加订单详情的订单号
        for (OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.insertMany(orderItemList);
        //减少商品库存
        this.reduceProductStock(orderItemList);
        //删除购物车选择的商品
        this.cleanSelectGoods(cartList);
        //组装返回数据
        OrderVo orderVo = this.setOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse<String> cancelOrder(Integer userId,Long orderNum){
        Order order = orderMapper.selectByUserIdAndOrderNum(userId,orderNum);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户不存在该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createByErrorMessage("用户订单已付款，请走退款流程取消");
        }
        order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int result = orderMapper.updateByPrimaryKeySelective(order);
        if (result == 0 ){
            return ServerResponse.createByErrorMessage("取消订单操作失败");
        }
        //修改商品库存数量
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNum(userId,orderNum);
        for (OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()+orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
        return ServerResponse.createBySuccessMessage("取消订单操作成功");
    }

    public ServerResponse<OrderVo> detailOrder(Integer userId,Long orderNum){
        Order order = orderMapper.selectByUserIdAndOrderNum(userId,orderNum);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户不存在该订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNum(userId,orderNum);
        OrderVo orderVo = setOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse<PageInfo> listOrder(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList =this.setOrderVoList(orderList,userId);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<OrderVo> setOrderVoList(List<Order> orderList , Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        if (userId != null){
            for (Order myOrder : orderList){
                List<OrderItem> orderItemList = Lists.newArrayList();
                orderItemList = orderItemMapper.selectByUserIdAndOrderNum(myOrder.getUserId(),myOrder.getOrderNo());
                OrderVo orderVo = setOrderVo(myOrder,orderItemList);
                orderVoList.add(orderVo);
            }
            return orderVoList;
        }else {
            for (Order myOrder : orderList){
                List<OrderItem> orderItemList = Lists.newArrayList();
                orderItemList = orderItemMapper.selectByOrderNum(myOrder.getOrderNo());
                OrderVo orderVo = setOrderVo(myOrder,orderItemList);
                orderVoList.add(orderVo);
            }
            return orderVoList;
        }




    }

    private OrderVo setOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(setShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));


        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));


        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = setOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private ShippingVo setShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    private OrderItemVo setOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private void cleanSelectGoods(List<Cart> cartList){
        for (Cart cart : cartList){
            if (cart.getChecked()==Const.Cart.CHECK_SELECT)
                cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order setOrder(Integer userId , Integer addressId , BigDecimal totalPrice){
        Order order = new Order();
        long orderNum = this.createOrderNum();
        order.setOrderNo(orderNum);
        order.setUserId(userId);
        order.setShippingId(addressId);
        order.setPayment(totalPrice);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPostage(0);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        int result = orderMapper.insert(order);
        if (result == 0 ){
            return null;
        }
        return order;
    }

    private long createOrderNum (){
        long orderNum = System.currentTimeMillis();
        return orderNum+(long)Math.random()*10;
    }

    private BigDecimal getTotalPrice(List<OrderItem> orderItemList){
        BigDecimal totalPrice = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList){
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return totalPrice;
    }

    private ServerResponse getOrderItemList (Integer userId , List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();

        for (Cart cart : cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus() != Const.Status.STATUS_SOLD){
                return ServerResponse.createBySuccessMessage("商品"+product.getName()+"已下架或删除");
            }
            if (product.getStock() < cart.getQuantity()){
                return ServerResponse.createBySuccessMessage("商品"+product.getName()+"库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }



    //后端
    public ServerResponse<PageInfo> backListOrder(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAll();
        List<OrderVo> orderVoList = this.setOrderVoList(orderList,null);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<OrderVo> backDetailOrder(Long orderNum){
       Order order = orderMapper.selectByOrderNum(orderNum);
       if (order == null ){
           return ServerResponse.createByErrorMessage("订单不存在");
       }
       List<OrderItem> orderItemList = orderItemMapper.selectByOrderNum(orderNum) ;
       OrderVo orderVo = this.setOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse<PageInfo> backSearchOrder(Long orderNum , int pageNum , int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNum(orderNum);
        if (order == null ){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNum(orderNum);
        OrderVo orderVo = setOrderVo(order,orderItemList);
        PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
        pageInfo.setList(Lists.newArrayList(orderVo));
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<String> backSendGoodsOrder(Long orderNum){
        Order order = orderMapper.selectByOrderNum(orderNum);
        if (order == null ){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            order.setSendTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
            return ServerResponse.createBySuccess("发货操作成功");
        }
        return ServerResponse.createByErrorMessage("订单状态异常");
    }



}

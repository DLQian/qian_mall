package com.mmall.services;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {

    ServerResponse pay(Integer userId, Long orderNum, String path);

    ServerResponse alipay(Map<String,String> myMap);

    ServerResponse queryOrderPayStatus(Integer userId,Long orderNum);

    ServerResponse createOrder(Integer userId,Integer addressId);

    ServerResponse<String> cancelOrder(Integer userId,Long orderNum);

    ServerResponse<OrderVo> detailOrder(Integer userId, Long orderNum);

    ServerResponse<PageInfo> listOrder(Integer userId, int pageNum, int pageSize);



    ServerResponse<PageInfo> backListOrder(int pageNum,int pageSize);

    ServerResponse<OrderVo> backDetailOrder(Long orderNum);

    ServerResponse<PageInfo> backSearchOrder(Long orderNum , int pageNum , int pageSize);

    ServerResponse<String> backSendGoodsOrder(Long orderNum);
}

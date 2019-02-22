package com.mmall.services;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartListVo;

public interface ICartService {

    ServerResponse<CartListVo> add(Integer userId , Integer productId , Integer count);

    ServerResponse<CartListVo> update(Integer userId ,Integer productId , Integer count);

    ServerResponse<CartListVo> delete(Integer userId ,String productIds);

    ServerResponse<CartListVo> find(Integer userId);

    ServerResponse<CartListVo> checkToSelectOrUnselect(Integer userId,Integer productId,Integer check);

    ServerResponse<Integer> getCartProudctCount(Integer userId);
}

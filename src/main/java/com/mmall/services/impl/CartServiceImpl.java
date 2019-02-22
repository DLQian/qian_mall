package com.mmall.services.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.services.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartListVo;
import com.mmall.vo.CartProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartListVo> add(Integer userId ,Integer productId , Integer count){
        if (productId == null || count == null){
            ServerResponse.createByErrorMessage("商品Id或商品数量参数错误");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if (cart == null){
            Cart assembleCart = new Cart();
            assembleCart.setUserId(userId);
            assembleCart.setProductId(productId);
            assembleCart.setQuantity(count);
            assembleCart.setChecked(Const.Cart.CHECK_SELECT);
            int result = cartMapper.insert(assembleCart);
        }else {
            cart.setQuantity(cart.getQuantity()+count);
            int result = cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartListVo cartListVo = this.getCartListVoLimit(userId);
        return ServerResponse.createBySuccess(cartListVo);
    }

    public ServerResponse<CartListVo> update(Integer userId ,Integer productId , Integer count){
        if (productId == null || count == null){
            return  ServerResponse.createByErrorMessage("商品Id或商品数量参数错误");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if (cart == null ){
            return ServerResponse.createByErrorMessage("购物车中未找到该商品");
        }
        cart.setQuantity(count);
        cartMapper.updateByPrimaryKey(cart);
        CartListVo cartListVo = this.getCartListVoLimit(userId);
        return ServerResponse.createBySuccess(cartListVo);
    }

    public ServerResponse<CartListVo> delete(Integer userId ,String productIds){
        if (productIds == null){
            return  ServerResponse.createByErrorMessage("商品Id或商品数量参数错误");
        }
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        cartMapper.deleteByUserIdAndProductIds(userId,productIdList);

        CartListVo cartListVo = this.getCartListVoLimit(userId);
        return ServerResponse.createBySuccess(cartListVo);
    }

    public ServerResponse<CartListVo> find(Integer userId){
        CartListVo cartListVo = this.getCartListVoLimit(userId);
        return ServerResponse.createBySuccess(cartListVo);
    }

    public ServerResponse<CartListVo> checkToSelectOrUnselect(Integer userId,Integer productId,Integer check){
        if (userId == null || check == null){
            return  ServerResponse.createByErrorMessage("用户Id或选择状态参数错误");
        }
        int resulte = cartMapper.updateSelectOrUnselectByUserId(userId,productId,check);
        if (resulte == 0 ){
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return this.find(userId);
    }

    public ServerResponse<Integer> getCartProudctCount(Integer userId){
        int resulte = cartMapper.selectCartProductCountByUserId(userId);
        return ServerResponse.createBySuccess(resulte);
    }

    //装配购物车信息
    public CartListVo getCartListVoLimit (Integer userId){
        CartListVo cartListVo = new CartListVo();
        //购物车列表信息初始化
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //购物车总价格初始化
        BigDecimal cartTotalPrice = new BigDecimal("0.00");
        String cartTotalPriceStr ;
        String cartTotalPriceString = null;
        //获取用户购物车信息
        List<Cart> cartList = cartMapper.selectByUserId(userId);

        if (!CollectionUtils.isEmpty(cartList)){
            for (Cart cart: cartList){
                //组装购物车内产品信息
                CartProductVo assembleCart = new CartProductVo();
                assembleCart.setId(cart.getId());
                assembleCart.setUserId(cart.getUserId());
                assembleCart.setProductId(cart.getProductId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null ){
                    //继续组装信息
                    assembleCart.setProductMainImage(product.getMainImage());
                    assembleCart.setProductName(product.getName());
                    assembleCart.setProductSubtitle(product.getSubtitle());
                    assembleCart.setProductStatus(product.getStatus());
                    assembleCart.setProductPrice(product.getPrice());
                    assembleCart.setProductStock(product.getStock());

                    //库存情况
                    int buyCount = 0;
                    if (product.getStock() >= cart.getQuantity() ){
                        //库存充足的时候
                        buyCount = cart.getQuantity();
                        assembleCart.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        //库存不足的时候
                        buyCount = product.getStock();
                        assembleCart.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        Cart cartSetQuantiy = new Cart();
                        cartSetQuantiy.setId(assembleCart.getId());
                        cartSetQuantiy.setQuantity(buyCount);
                        cartMapper.updateByPrimaryKeySelective(cartSetQuantiy);
                    }
                    assembleCart.setQuantity(buyCount);
                    //计算单一商品总价
                    assembleCart.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),assembleCart.getQuantity()));

                    assembleCart.setProductTotalPriceString(BigDecimalUtil.mul(product.getPrice().doubleValue(),assembleCart.getQuantity()).toString());

                    assembleCart.setProductChecked(cart.getChecked());
                }
                //计算整个购物车的商品总
                if (cart.getChecked() == Const.Cart.CHECK_SELECT){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),assembleCart.getProductTotalPrice().doubleValue());
                    cartTotalPrice = BigDecimalUtil.div(cartTotalPrice.doubleValue(),1.00);
                    cartTotalPriceString = cartTotalPrice.toString();

                }
                cartProductVoList.add(assembleCart);

            }
        }
        cartListVo.setCartTotalPrice(cartTotalPrice);
        cartListVo.setCartTotalPriceString(cartTotalPriceString);
        cartListVo.setCartProductVoList(cartProductVoList);
        cartListVo.setAllChecked(this.getAllCheckStatus(userId));

        //图片
        CartProductVo cpv = cartProductVoList.get(0);
        cartListVo.setImageHost(cpv.getProductMainImage());
        return cartListVo;
    }

    private boolean getAllCheckStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}

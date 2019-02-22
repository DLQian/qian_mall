package com.mmall.services.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.services.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("iShipping")
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId , Shipping address){
        address.setUserId(userId);
        int result = shippingMapper.insert(address);
        if (result == 0 ){
            return ServerResponse.createByErrorMessage("添加地址失败");
        }
        return ServerResponse.createBySuccess("添加地址成功",address.getId());
    }

    public ServerResponse delete(Integer userId ,Integer addressId){
        //使用userId和addressId 精确查找防止横向越权
        int result = shippingMapper.deleteByUserIdAndAddressId(userId,addressId);
        if (result == 0 ){
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
        return ServerResponse.createBySuccess("删除地址成功");
    }

    public ServerResponse update(Integer userId , Shipping address){
        address.setUserId(userId);
        int result = shippingMapper.updateByAddress(address);
        if (result == 0 ){
            return ServerResponse.createByErrorMessage("更新地址失败");
        }
        return ServerResponse.createBySuccess("更新地址成功",address.getId());
    }

    public ServerResponse find(Integer userId,Integer addressId){
        Shipping result = shippingMapper.selectByUserIdAndAddressId(userId,addressId);
        if (result == null ){
            return ServerResponse.createByErrorMessage("查询地址失败");
        }
        return ServerResponse.createBySuccess("查询地址成功",result);
    }

    public ServerResponse<List<Shipping>> list(Integer userId){
        List<Shipping> result = shippingMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(result)){
            return ServerResponse.createByErrorMessage("该用户暂无地址信息");
        }else{
            return ServerResponse.createBySuccess(result);
        }
    }
}

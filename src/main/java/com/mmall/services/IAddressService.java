package com.mmall.services;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.List;

public interface IAddressService {

    ServerResponse add(Integer userId , Shipping shipping);

    ServerResponse delete(Integer userId ,Integer addressId);

    ServerResponse update(Integer userId , Shipping address);

    ServerResponse find(Integer userId,Integer addressId);

    ServerResponse<List<Shipping>> list(Integer userId);

}

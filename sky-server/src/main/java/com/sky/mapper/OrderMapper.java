package com.sky.mapper;


import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 提交订单
     * @param orders
     */
    void submitOrder(Orders orders);
}

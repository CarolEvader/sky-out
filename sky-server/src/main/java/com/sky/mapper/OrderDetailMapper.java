package com.sky.mapper;


import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单详细数据
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单详细
     * @param OrderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{OrderId}")
    List<OrderDetail> getByOrderId(Long OrderId);
}

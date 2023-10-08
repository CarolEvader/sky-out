package com.sky.mapper;


import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {


    /**
     * 获取营业额
     * @param map
     * @return
     */
    Double getTurnoverByTimeAndStatus(Map map);

    /**
     * 获取用户数
     * @param map
     * @return
     */
    Integer getUserCount(Map map);

    /**
     * 获取订单数
     * @param map
     * @return
     */
    Integer getOrderCount(Map map);

    /**
     * 查询Top10
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getTop10(LocalDateTime begin, LocalDateTime end);
}

package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void insertBatch(List<OrderDetail> orderDetails);

    List<OrderDetail> getByOrderId(List<Long> orderIds);
    @Select("SELECT *FROM order_detail WHERE order_id=#{id}")
    List<OrderDetail> nameByOrderId(Long id);
}

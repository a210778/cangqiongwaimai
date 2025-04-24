package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderHistoryVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    @Select("select *from orders where id=#{id}")
    Orders getById(Integer id);

    Page<OrderVO> adminPageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    @Select("select count(*) from orders where status=#{i}")
    Integer getStatus(int i);
    @Select("SELECT name FROM order_detail WHERE order_id = #{orderId}")
    List<String> getDishNamesByOrderId(Long orderId);
    @Select("select *from orders where status=#{status} and order_time < #{time}")
    List<Orders> getStatusAndTimeOutTL(Integer status, LocalDateTime time);

    @Update("UPDATE orders SET status = #{status}, checkout_time = NOW() WHERE number = #{number}")
    void updateStatusByOrderNumber(String number, Integer status);

    @Select("SELECT IFNULL(SUM(amount), 0) FROM orders WHERE status = #{status} AND checkout_time >= #{begin} AND checkout_time < #{end}")
    Double sumTurnover(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end, @Param("status") Integer status);
    @Select("SELECT COUNT(*) FROM orders WHERE order_time>#{startOfDay} and order_time<#{endOfDay}")
    Long getByTime(LocalDateTime startOfDay, LocalDateTime endOfDay);
    @Select("SELECT COUNT(*) FROM orders WHERE order_time>#{startOfDay} and order_time<#{endOfDay} and status= #{completed}")
    Long getByTimeAndStatus(LocalDateTime startOfDay, LocalDateTime endOfDay, Integer completed);
    @Select("SELECT * FROM orders WHERE order_time>#{startOfDay} and order_time<#{endOfDay} and status= #{completed}")
    List<Orders> getByTimeAndStatusAndDish(LocalDate startOfDay, LocalDate endOfDay, Integer completed);

    Integer countByMap(Map map);

    Double sumByMap(Map map);
}

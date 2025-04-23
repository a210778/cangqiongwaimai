package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

import java.util.List;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO getById(Integer id);

    void cancelOrder(Integer id);

    void repetition(Integer id);

    PageResult adminPageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void delivery(Integer id);

    void complete(Integer id);

    void reminder(Long id);
}

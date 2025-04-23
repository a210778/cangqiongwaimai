package com.sky.task;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j

public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;


    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {

        log.info("处理超时订单{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
       List<Orders> ordersList = orderMapper.getStatusAndTimeOutTL(Orders.PAID,time);
       if (ordersList != null && ordersList.size() > 0) {
           for (Orders orders : ordersList) {
               orders.setStatus(Orders.CANCELLED);
               orders.setCancelReason("订单超时");
               orders.setCancelTime(LocalDateTime.now());
               orderMapper.update(orders);
           }
       }

    }
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrderTL() {
          log.info("处理超时派送订单{}", LocalDateTime.now());
          LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getStatusAndTimeOutTL(Orders.DELIVERY_IN_PROGRESS, time);
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("派送超时，自动完成");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }
}

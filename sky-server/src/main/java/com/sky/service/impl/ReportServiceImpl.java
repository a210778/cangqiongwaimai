package com.sky.service.impl;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO turnOverReport(LocalDate begin, LocalDate end) {
        List<String> dateList = new ArrayList<>();
        List<String> turnoverList = new ArrayList<>();

        for(LocalDate date = begin; date.isBefore(end); date = date.plusDays(1)){
            dateList.add(date.toString());
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            BigDecimal turnover = orderMapper.sumTurnover(startOfDay, endOfDay, Orders.COMPLETED);
            turnoverList.add(turnover == null ? "0" : turnover.toString());
        }
        TurnoverReportVO vo = new TurnoverReportVO();
        vo.setDateList(String.join(",", dateList));
        vo.setTurnoverList(String.join(",", turnoverList));
        return vo;

    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<String> dateList = new ArrayList<>();
        List<String> newUserList = new ArrayList<>();
        List<String> totaluserList = new ArrayList<>();
        for (LocalDate date=begin; date.isBefore(end); date = date.plusDays(1)){
            dateList.add(date.toString());
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            Long newListUser =userMapper.getByTime(startOfDay,endOfDay);
            Long totalListUser =userMapper.getByTimeTotal(endOfDay);
            newUserList.add(newListUser == null ? "0" : newListUser.toString());
            totaluserList.add(totalListUser == null ? "0" : totalListUser.toString());
        }
        UserReportVO vo = new UserReportVO();
        vo.setDateList(String.join(",", dateList));
        vo.setNewUserList(String.join(",", newUserList));
        vo.setTotalUserList(String.join(",", totaluserList));
        return vo;
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<String> dateList = new ArrayList<>();
        List<String> orderList = new ArrayList<>();
        List<String> validOrderList = new ArrayList<>();
        for (LocalDate date =begin; date.isBefore(end); date = date.plusDays(1)){
            dateList.add(date.toString());
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            Long orderCountList = orderMapper.getByTime(startOfDay,endOfDay);
            orderList.add(orderCountList == null ? "0" : orderCountList.toString());
            Long validOrderCountList = orderMapper.getByTimeAndStatus(startOfDay,endOfDay,Orders.COMPLETED);
            validOrderList.add(validOrderCountList == null ? "0" : validOrderCountList.toString());
        }
        Long totalOrderCount = orderMapper.getByTime(begin.atStartOfDay(),end.atStartOfDay());
        Long validOrderCount = orderMapper.getByTimeAndStatus(begin.atStartOfDay(),end.atStartOfDay(),Orders.COMPLETED);
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : (double) validOrderCount / totalOrderCount;
        OrderReportVO vo = new OrderReportVO();
        vo.setDateList(String.join(",", dateList));
        vo.setOrderCountList(String.join(",", orderList));
        vo.setValidOrderCountList(String.join(",", validOrderList));
        vo.setTotalOrderCount(Math.toIntExact(totalOrderCount));
        vo.setValidOrderCount(Math.toIntExact(validOrderCount));
        vo.setOrderCompletionRate(orderCompletionRate);

        return vo;
    }

    @Override
    public SalesTop10ReportVO top10Report(LocalDate begin, LocalDate end) {
        List<String> nameList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();
        List<Orders> ordersList = orderMapper.getByTimeAndStatusAndDish(begin, end, Orders.COMPLETED);
        Map<String, Integer> dishSalesMap = new HashMap<>();
        for (Orders orders : ordersList) {
            List<OrderDetail> orderDetailList = orderDetailMapper.nameByOrderId(orders.getId());
            for (OrderDetail orderDetail : orderDetailList) {
                String name = orderDetail.getName();
                Integer number = orderDetail.getNumber(); // 数量

                dishSalesMap.put(name, dishSalesMap.getOrDefault(name, 0) + number);
            }
        }
        // 1. 将 map 转为 list 并按销量降序排序
        List<Map.Entry<String, Integer>> sortedList = dishSalesMap.entrySet()
                    .stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // 按销量降序
                    .limit(10) // 取前10名
                    .collect(Collectors.toList());

            // 2. 放入 nameList 和 numberList
            for (Map.Entry<String, Integer> entry : sortedList) {
                nameList.add(entry.getKey()); // 菜品名
                numberList.add(String.valueOf(entry.getValue())); // 销量转字符串
            }

            // 3. 组装返回对象
            SalesTop10ReportVO vo = new SalesTop10ReportVO();
            vo.setNameList(String.join(",", nameList));
            vo.setNumberList(String.join(",", numberList));
            return vo;

    }



}

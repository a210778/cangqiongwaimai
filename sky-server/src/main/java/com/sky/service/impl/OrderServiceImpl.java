package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.WebSocket.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //判断是否有异常
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> cartList = shoppingCartMapper.getById(userId);
        if (cartList == null || cartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //插入一条订单信息
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());
        orders.setUserId(userId);
        log.info("插入的数据{}", orders);
        try {
            orderMapper.insert(orders);
        } catch (Exception e) {
            log.error("插入订单异常", e);  // 或者 log.error("插入订单异常", e);
            throw e;
        }
        //插入n条订单详情信息
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart : cartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);

        //清空当前购物车
        shoppingCartMapper.clean(userId);

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderTime(orders.getOrderTime());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderAmount(orders.getAmount());
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
//    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        // 当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.getById(userId);
//
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
//
//        return vo;
//    }
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        // 模拟点击支付成功（不调用微信接口）
        // 你可以这里做一些状态检查，比如判断订单是否已经支付等（可选）

        // 构造一个模拟的支付响应
        OrderPaymentVO vo = new OrderPaymentVO();
        vo.setNonceStr("模拟随机字符串");
        vo.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
        vo.setPackageStr("prepay_id=模拟预支付ID");
        vo.setPaySign("模拟PaySign");
        vo.setSignType("MD5");

        orderMapper.updateStatusByOrderNumber(ordersPaymentDTO.getOrderNumber(), Orders.REFUND);

        return vo;
    }


    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        System.out.println("lllalalalallalalallalal");
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        Map map = new HashMap();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号:" + outTradeNo);

        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);

    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 1. 查询订单分页
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<Orders> ordersList = page.getResult();

        // 2. 提取所有订单 ID
        List<Long> orderIds = ordersList.stream().map(Orders::getId).collect(Collectors.toList());

        // 3. 查询所有订单的详情
        List<OrderDetail> detailList = orderDetailMapper.getByOrderId(orderIds);

        // 4. 把详情按 orderId 分组
        Map<Long, List<OrderDetail>> detailMap = detailList.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        // 5. 构造 VO 列表
        List<OrderHistoryVO> voList = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrderHistoryVO vo = new OrderHistoryVO();
            BeanUtils.copyProperties(orders, vo);
            vo.setOrderDetailList(detailMap.getOrDefault(orders.getId(), new ArrayList<>()));
            voList.add(vo);
        }

        return new PageResult(page.getTotal(), voList);

    }

    @Override
    public OrderVO getById(Integer id) {
        Orders orders =orderMapper.getById(id);
        List<Long> list = new ArrayList<>();
        list.add(orders.getId());
        List<OrderDetail> orderDetailList =orderDetailMapper.getByOrderId(list);
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(orders, vo);
        vo.setOrderDetailList(orderDetailList);
        return vo;
    }

    @Override
    public void cancelOrder(Integer id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.CANCELLED);
        orderMapper.update(orders);
    }

    @Override
    @Transactional
    public void repetition(Integer id) {
        // 1. 查询旧订单
        Orders oldOrder = orderMapper.getById(id);
        if (oldOrder == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        List<Long> list = new ArrayList<>();
        list.add(oldOrder.getId());
        // 2. 查询订单明细
        List<OrderDetail> oldDetails = orderDetailMapper.getByOrderId(list);

        // 3. 构造新订单
        Orders newOrder = new Orders();
        BeanUtils.copyProperties(oldOrder, newOrder);
        newOrder.setId(null); // 新订单要生成新 ID
        newOrder.setNumber(String.valueOf(System.currentTimeMillis())); // 或者用雪花算法生成
        newOrder.setStatus(Orders.PENDING_PAYMENT); // 设置为待付款
        newOrder.setOrderTime(LocalDateTime.now());
        newOrder.setCheckoutTime(null);
        newOrder.setCancelTime(null);
        newOrder.setDeliveryTime(null);
        newOrder.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(60)); // 预估1小时后送达
        newOrder.setPayStatus(Orders.UN_PAID);
        newOrder.setRemark(""); // 重置备注
        orderMapper.insert(newOrder);

        // 4. 复制订单明细
        List<OrderDetail> newDetails = new ArrayList<>();
        for (OrderDetail oldDetail : oldDetails) {
            OrderDetail newDetail = new OrderDetail();
            BeanUtils.copyProperties(oldDetail, newDetail);
            newDetail.setId(null);
            newDetail.setOrderId(newOrder.getId());
            newDetails.add(newDetail);
        }
        orderDetailMapper.insertBatch(newDetails);


    }

    @Override
    public PageResult adminPageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.adminPageQuery(ordersPageQueryDTO);
        for (OrderVO orderVO : page.getResult()) {
            List<String> dishNames = orderMapper.getDishNamesByOrderId(orderVO.getId());
            orderVO.setOrderDishes(String.join(", ", dishNames));
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public OrderStatisticsVO statistics() {
        Integer confirmed = orderMapper.getStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.getStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer toBeConfirmed = orderMapper.getStatus(Orders.TO_BE_CONFIRMED);
        OrderStatisticsVO vo = new OrderStatisticsVO();
        vo.setConfirmed(confirmed);
        vo.setDeliveryInProgress(deliveryInProgress);
        vo.setToBeConfirmed(toBeConfirmed);
        return vo;
    }

    @Override
    public void adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.getById(Math.toIntExact(ordersCancelDTO.getId()));
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orderMapper.update(orders);

    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = orderMapper.getById(Math.toIntExact(ordersConfirmDTO.getId()));
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.getById(Math.toIntExact(ordersRejectionDTO.getId()));
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Integer id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    @Override
    public void complete(Integer id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);

    }

    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(Math.toIntExact(id));
        if (orders==null)
        {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        }
        Map map = new HashMap();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content","用户催单");
        String jsonString = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

}

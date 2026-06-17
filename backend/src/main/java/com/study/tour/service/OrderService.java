package com.study.tour.service;

import com.study.tour.dto.CreateOrderRequest;
import com.study.tour.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(CreateOrderRequest request);
    OrderDTO getOrderByOutTradeNo(String outTradeNo);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getOrdersByStudentId(Long studentId);
    boolean cancelOrder(Long orderId);
    boolean refundOrder(Long orderId);
}

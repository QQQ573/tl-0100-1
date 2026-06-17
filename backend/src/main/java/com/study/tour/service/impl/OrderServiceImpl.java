package com.study.tour.service.impl;

import com.study.tour.dto.CreateOrderRequest;
import com.study.tour.dto.OrderDTO;
import com.study.tour.dto.OrderItemDTO;
import com.study.tour.entity.*;
import com.study.tour.enums.OrderStatus;
import com.study.tour.repository.*;
import com.study.tour.service.OrderService;
import com.study.tour.service.SupplementOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SupplementOrderRepository supplementOrderRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        Student student = studentRepository.findByIdCard(request.getStudentIdCard())
                .orElseGet(() -> {
                    Student newStudent = Student.builder()
                            .name(request.getStudentName())
                            .idCard(request.getStudentIdCard())
                            .phone(request.getStudentPhone())
                            .email(request.getStudentEmail())
                            .hasInsurance(false)
                            .build();
                    return studentRepository.save(newStudent);
                });

        String outTradeNo = generateOutTradeNo();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("产品不存在: " + itemReq.getProductId()));

            if (!"ACTIVE".equals(product.getStatus())) {
                throw new RuntimeException("产品已下架: " + product.getName());
            }

            BigDecimal itemAmount = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(itemReq.getQuantity())
                    .build();
            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .outTradeNo(outTradeNo)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING.name())
                .studentId(student.getId())
                .guardianName(request.getGuardianName())
                .guardianIdCard(request.getGuardianIdCard())
                .guardianPhone(request.getGuardianPhone())
                .campStartDate(request.getCampStartDate())
                .build();

        order = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
        }

        return convertToDTO(order, orderItems);
    }

    @Override
    public OrderDTO getOrderByOutTradeNo(String outTradeNo) {
        Order order = orderRepository.findByOutTradeNo(outTradeNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return convertToDTO(order, items);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return convertToDTO(order, items);
    }

    @Override
    public List<OrderDTO> getOrdersByStudentId(Long studentId) {
        List<Order> orders = orderRepository.findByStudentId(studentId);
        return orders.stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            return convertToDTO(order, items);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new RuntimeException("只有待支付订单可以取消");
        }

        order.setStatus(OrderStatus.CANCELLED.name());
        orderRepository.save(order);
        return true;
    }

    @Override
    @Transactional
    public boolean refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new RuntimeException("只有已支付订单可以退订");
        }

        if (order.getCampStartDate() != null && 
            !order.getCampStartDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("已开营的订单不可退订");
        }

        if (supplementOrderRepository.existsByParentOrderIdAndStatus(orderId, "PENDING")) {
            throw new RuntimeException("存在私募补差在途，暂不可退订");
        }

        order.setStatus(OrderStatus.REFUNDED.name());
        orderRepository.save(order);

        Student student = studentRepository.findById(order.getStudentId()).orElse(null);
        if (student != null) {
            student.setHasInsurance(false);
            student.setInsuranceBadgeUrl(null);
            studentRepository.save(student);
        }

        return true;
    }

    private String generateOutTradeNo() {
        return "ST" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderDTO convertToDTO(Order order, List<OrderItem> items) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setItems(items.stream().map(this::convertItemToDTO).collect(Collectors.toList()));

        if (order.getStudentId() != null) {
            studentRepository.findById(order.getStudentId()).ifPresent(student -> {
                dto.setStudentName(student.getName());
            });
        }

        return dto;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }
}

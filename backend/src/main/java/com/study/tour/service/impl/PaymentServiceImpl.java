package com.study.tour.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.tour.dto.PaymentCallbackRequest;
import com.study.tour.entity.Order;
import com.study.tour.entity.Payment;
import com.study.tour.entity.Student;
import com.study.tour.enums.OrderStatus;
import com.study.tour.enums.PaymentStatus;
import com.study.tour.repository.OrderRepository;
import com.study.tour.repository.PaymentRepository;
import com.study.tour.repository.StudentRepository;
import com.study.tour.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String mockPay(String outTradeNo) {
        Order order = orderRepository.findByOutTradeNo(outTradeNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不支持支付");
        }

        return "模拟支付链接: /payment/mock/callback?outTradeNo=" + outTradeNo + "&tradeStatus=SUCCESS";
    }

    @Override
    @Transactional
    public String handleCallback(PaymentCallbackRequest request) {
        String outTradeNo = request.getOutTradeNo();

        if (paymentRepository.existsByOutTradeNoAndStatus(outTradeNo, PaymentStatus.SUCCESS.name())) {
            return "success";
        }

        Order order = orderRepository.findByOutTradeNo(outTradeNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        Payment payment = Payment.builder()
                .outTradeNo(outTradeNo)
                .amount(order.getTotalAmount())
                .status("SUCCESS".equals(request.getTradeStatus()) 
                        ? PaymentStatus.SUCCESS.name() 
                        : PaymentStatus.FAILED.name())
                .payTime(LocalDateTime.now())
                .callbackContent(toJson(request))
                .build();
        paymentRepository.save(payment);

        if ("SUCCESS".equals(request.getTradeStatus())) {
            if (OrderStatus.PENDING.name().equals(order.getStatus())) {
                order.setStatus(OrderStatus.PAID.name());
                order.setPaidAt(LocalDateTime.now());
                orderRepository.save(order);

                Student student = studentRepository.findById(order.getStudentId()).orElse(null);
                if (student != null) {
                    student.setHasInsurance(true);
                    student.setInsuranceBadgeUrl("/api/pdf/insurance-badge/" + order.getId());
                    studentRepository.save(student);
                }
            }
        }

        return "success";
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

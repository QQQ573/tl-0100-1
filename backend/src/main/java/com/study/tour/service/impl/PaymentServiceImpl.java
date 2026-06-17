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
import com.study.tour.service.SupplementOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private SupplementOrderService supplementOrderService;

    @Override
    public String mockPay(String outTradeNo) {
        if (outTradeNo.startsWith("SP")) {
            return "模拟补差支付链接: /payment/mock/callback?outTradeNo=" + outTradeNo + "&tradeStatus=SUCCESS";
        }

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

        if (outTradeNo.startsWith("SP")) {
            return handleSupplementCallback(request);
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

    private String handleSupplementCallback(PaymentCallbackRequest request) {
        String outTradeNo = request.getOutTradeNo();

        if (!"SUCCESS".equals(request.getTradeStatus())) {
            Payment payment = Payment.builder()
                    .outTradeNo(outTradeNo)
                    .amount(BigDecimal.ZERO)
                    .status(PaymentStatus.FAILED.name())
                    .payTime(LocalDateTime.now())
                    .callbackContent(toJson(request))
                    .build();
            paymentRepository.save(payment);
            return "success";
        }

        supplementOrderService.handleSupplementPaymentSuccess(outTradeNo);

        Payment payment = Payment.builder()
                .outTradeNo(outTradeNo)
                .amount(BigDecimal.ZERO)
                .status(PaymentStatus.SUCCESS.name())
                .payTime(LocalDateTime.now())
                .callbackContent(toJson(request))
                .build();
        paymentRepository.save(payment);

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

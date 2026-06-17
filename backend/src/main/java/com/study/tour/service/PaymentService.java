package com.study.tour.service;

import com.study.tour.dto.PaymentCallbackRequest;

public interface PaymentService {
    String mockPay(String outTradeNo);
    String handleCallback(PaymentCallbackRequest request);
}

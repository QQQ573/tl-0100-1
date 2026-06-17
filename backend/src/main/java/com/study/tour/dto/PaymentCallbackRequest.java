package com.study.tour.dto;

import lombok.Data;

@Data
public class PaymentCallbackRequest {
    private String outTradeNo;
    private String amount;
    private String tradeStatus;
    private String tradeNo;
    private String sign;
}

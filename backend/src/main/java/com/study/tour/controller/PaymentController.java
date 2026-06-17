package com.study.tour.controller;

import com.study.tour.common.Result;
import com.study.tour.dto.PaymentCallbackRequest;
import com.study.tour.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "支付管理", description = "支付相关接口")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/mock/pay/{outTradeNo}")
    @Operation(summary = "模拟支付（获取支付链接）")
    public Result<String> mockPay(@PathVariable String outTradeNo) {
        return Result.success(paymentService.mockPay(outTradeNo));
    }

    @GetMapping("/mock/callback")
    @Operation(summary = "模拟支付回调（幂等）")
    public String mockCallback(PaymentCallbackRequest request) {
        return paymentService.handleCallback(request);
    }

    @PostMapping("/callback")
    @Operation(summary = "支付回调接口（幂等）")
    public String callback(@RequestBody PaymentCallbackRequest request) {
        return paymentService.handleCallback(request);
    }
}

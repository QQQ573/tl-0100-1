package com.study.tour.controller;

import com.study.tour.common.Result;
import com.study.tour.dto.CreateOrderRequest;
import com.study.tour.dto.OrderDTO;
import com.study.tour.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关接口")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单")
    public Result<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public Result<OrderDTO> getOrderById(@PathVariable Long id) {
        return Result.success(orderService.getOrderById(id));
    }

    @GetMapping("/outTradeNo/{outTradeNo}")
    @Operation(summary = "根据订单号获取订单")
    public Result<OrderDTO> getOrderByOutTradeNo(@PathVariable String outTradeNo) {
        return Result.success(orderService.getOrderByOutTradeNo(outTradeNo));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学员的订单列表")
    public Result<List<OrderDTO>> getOrdersByStudentId(@PathVariable Long studentId) {
        return Result.success(orderService.getOrdersByStudentId(studentId));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<Boolean> cancelOrder(@PathVariable Long id) {
        return Result.success(orderService.cancelOrder(id));
    }

    @PutMapping("/{id}/refund")
    @Operation(summary = "退订订单（需校验未开营且无私募补差在途）")
    public Result<Boolean> refundOrder(@PathVariable Long id) {
        return Result.success(orderService.refundOrder(id));
    }
}

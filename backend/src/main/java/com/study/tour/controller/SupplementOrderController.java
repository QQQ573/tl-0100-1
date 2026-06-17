package com.study.tour.controller;

import com.study.tour.common.Result;
import com.study.tour.dto.CreateSupplementRequest;
import com.study.tour.dto.SupplementOrderDTO;
import com.study.tour.dto.UpgradePreviewDTO;
import com.study.tour.service.SupplementOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplement")
@Tag(name = "补差升档", description = "补差单相关接口")
@CrossOrigin(origins = "*")
public class SupplementOrderController {

    @Autowired
    private SupplementOrderService supplementOrderService;

    @GetMapping("/preview/{parentOrderId}")
    @Operation(summary = "获取升档预览（可选档位与差价）")
    public Result<UpgradePreviewDTO> getUpgradePreview(@PathVariable Long parentOrderId) {
        return Result.success(supplementOrderService.getUpgradePreview(parentOrderId));
    }

    @PostMapping
    @Operation(summary = "创建补差单")
    public Result<SupplementOrderDTO> createSupplementOrder(@Valid @RequestBody CreateSupplementRequest request) {
        return Result.success(supplementOrderService.createSupplementOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取补差单详情")
    public Result<SupplementOrderDTO> getSupplementById(@PathVariable Long id) {
        return Result.success(supplementOrderService.getSupplementByNo(String.valueOf(id)));
    }

    @GetMapping("/parent/{parentOrderId}")
    @Operation(summary = "获取父订单的补差单列表")
    public Result<List<SupplementOrderDTO>> getSupplementsByParentOrderId(@PathVariable Long parentOrderId) {
        return Result.success(supplementOrderService.getSupplementsByParentOrderId(parentOrderId));
    }
}

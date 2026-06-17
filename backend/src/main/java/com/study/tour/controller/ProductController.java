package com.study.tour.controller;

import com.study.tour.common.Result;
import com.study.tour.dto.ProductDTO;
import com.study.tour.enums.ProductType;
import com.study.tour.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "产品管理", description = "产品相关接口")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "获取所有产品")
    public Result<List<ProductDTO>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "按类型获取产品")
    public Result<List<ProductDTO>> getProductsByType(@PathVariable ProductType type) {
        return Result.success(productService.getProductsByType(type));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取产品详情")
    public Result<ProductDTO> getProductById(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }
}

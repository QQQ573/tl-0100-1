package com.study.tour.service;

import com.study.tour.dto.ProductDTO;
import com.study.tour.enums.ProductType;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByType(ProductType type);
    ProductDTO getProductById(Long id);
}

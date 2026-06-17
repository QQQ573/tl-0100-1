package com.study.tour.service;

import com.study.tour.dto.CreateSupplementRequest;
import com.study.tour.dto.SupplementOrderDTO;
import com.study.tour.dto.UpgradePreviewDTO;

import java.util.List;

public interface SupplementOrderService {
    UpgradePreviewDTO getUpgradePreview(Long parentOrderId);
    SupplementOrderDTO createSupplementOrder(CreateSupplementRequest request);
    SupplementOrderDTO getSupplementByNo(String supplementNo);
    List<SupplementOrderDTO> getSupplementsByParentOrderId(Long parentOrderId);
    boolean handleSupplementPaymentSuccess(String supplementNo);
}

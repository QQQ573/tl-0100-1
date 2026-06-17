package com.study.tour.service.impl;

import com.study.tour.dto.*;
import com.study.tour.entity.Order;
import com.study.tour.entity.OrderItem;
import com.study.tour.entity.Product;
import com.study.tour.entity.SupplementOrder;
import com.study.tour.enums.OrderStatus;
import com.study.tour.enums.ProductLevel;
import com.study.tour.enums.ProductType;
import com.study.tour.repository.OrderItemRepository;
import com.study.tour.repository.OrderRepository;
import com.study.tour.repository.ProductRepository;
import com.study.tour.repository.SupplementOrderRepository;
import com.study.tour.service.SupplementOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplementOrderServiceImpl implements SupplementOrderService {

    @Autowired
    private SupplementOrderRepository supplementOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final Map<String, Map<String, BigDecimal>> COVERAGE_MAP = new HashMap<>();
    static {
        Map<String, BigDecimal> basicCoverage = new LinkedHashMap<>();
        basicCoverage.put("意外身故/伤残", new BigDecimal("200000"));
        basicCoverage.put("意外医疗费用", new BigDecimal("20000"));
        basicCoverage.put("紧急救援服务", new BigDecimal("50000"));
        COVERAGE_MAP.put("BASIC", basicCoverage);

        Map<String, BigDecimal> standardCoverage = new LinkedHashMap<>();
        standardCoverage.put("意外身故/伤残", new BigDecimal("500000"));
        standardCoverage.put("意外医疗费用", new BigDecimal("50000"));
        standardCoverage.put("紧急救援服务", new BigDecimal("100000"));
        standardCoverage.put("行程取消损失", new BigDecimal("5000"));
        COVERAGE_MAP.put("STANDARD", standardCoverage);

        Map<String, BigDecimal> premiumCoverage = new LinkedHashMap<>();
        premiumCoverage.put("意外身故/伤残", new BigDecimal("1000000"));
        premiumCoverage.put("意外医疗费用", new BigDecimal("100000"));
        premiumCoverage.put("紧急救援服务", new BigDecimal("200000"));
        premiumCoverage.put("行程取消损失", new BigDecimal("10000"));
        COVERAGE_MAP.put("PREMIUM", premiumCoverage);
    }

    @Override
    public UpgradePreviewDTO getUpgradePreview(Long parentOrderId) {
        Order order = orderRepository.findById(parentOrderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new RuntimeException("只有已支付订单可以补差升档");
        }

        if (order.getCampStartDate() != null && !order.getCampStartDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("已开营的订单不可补差升档");
        }

        if (supplementOrderRepository.existsByParentOrderIdAndStatus(parentOrderId, "PENDING")) {
            throw new RuntimeException("存在待支付的补差单，请先完成或取消");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(parentOrderId);
        String currentLevel = detectCurrentInsuranceLevel(items);

        if (currentLevel == null) {
            throw new RuntimeException("订单中未包含保险产品，无法补差升档");
        }

        List<Product> insuranceProducts = productRepository.findByTypeAndStatus(ProductType.INSURANCE, "ACTIVE");

        List<UpgradeOptionDTO> options = new ArrayList<>();
        for (Product p : insuranceProducts) {
            int levelCompare = compareLevel(p.getLevel(), currentLevel);
            if (levelCompare > 0) {
                UpgradeOptionDTO opt = new UpgradeOptionDTO();
                opt.setProductId(p.getId());
                opt.setLevel(p.getLevel());
                opt.setProductName(p.getName());
                opt.setPrice(p.getPrice());

                BigDecimal currentPrice = findInsurancePrice(items, insuranceProducts);
                if (currentPrice == null) {
                    currentPrice = findInsurancePriceByLevel(currentLevel, insuranceProducts);
                }
                BigDecimal diff = p.getPrice().subtract(currentPrice != null ? currentPrice : BigDecimal.ZERO);
                opt.setDiffAmount(diff.compareTo(BigDecimal.ZERO) > 0 ? diff : BigDecimal.ZERO);
                options.add(opt);
            }
        }

        options.sort(Comparator.comparing(o -> compareLevel(o.getLevel(), currentLevel)));

        Product currentProduct = insuranceProducts.stream()
                .filter(p -> currentLevel.equals(p.getLevel()))
                .findFirst().orElse(null);

        UpgradePreviewDTO preview = new UpgradePreviewDTO();
        preview.setParentOrderId(parentOrderId);
        preview.setCurrentLevel(currentLevel);
        preview.setCurrentProductName(currentProduct != null ? currentProduct.getName() : currentLevel);
        preview.setCurrentPrice(currentProduct != null ? currentProduct.getPrice() : BigDecimal.ZERO);
        preview.setUpgradeOptions(options);

        return preview;
    }

    @Override
    @Transactional
    public SupplementOrderDTO createSupplementOrder(CreateSupplementRequest request) {
        Order order = orderRepository.findById(request.getParentOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new RuntimeException("只有已支付订单可以补差升档");
        }

        if (order.getCampStartDate() != null && !order.getCampStartDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("已开营的订单不可补差升档");
        }

        if (supplementOrderRepository.existsByParentOrderIdAndStatus(request.getParentOrderId(), "PENDING")) {
            throw new RuntimeException("存在待支付的补差单，请先完成或取消");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(request.getParentOrderId());
        String currentLevel = detectCurrentInsuranceLevel(items);
        if (currentLevel == null) {
            throw new RuntimeException("订单中未包含保险产品，无法补差升档");
        }

        int levelCompare = compareLevel(request.getToLevel(), currentLevel);
        if (levelCompare <= 0) {
            throw new RuntimeException("只能升档，不能降档或平级");
        }

        List<Product> insuranceProducts = productRepository.findByTypeAndStatus(ProductType.INSURANCE, "ACTIVE");
        Product fromProduct = insuranceProducts.stream()
                .filter(p -> currentLevel.equals(p.getLevel()))
                .findFirst().orElseThrow(() -> new RuntimeException("原档位产品不存在"));
        Product toProduct = insuranceProducts.stream()
                .filter(p -> request.getToLevel().equals(p.getLevel()))
                .findFirst().orElseThrow(() -> new RuntimeException("目标档位产品不存在"));

        BigDecimal diffAmount = toProduct.getPrice().subtract(fromProduct.getPrice());
        if (diffAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("差价计算异常，差价必须大于0");
        }

        String coverageJson = buildCoverageJson(request.getToLevel());

        SupplementOrder supplement = SupplementOrder.builder()
                .supplementNo(generateSupplementNo())
                .parentOrderId(request.getParentOrderId())
                .fromLevel(currentLevel)
                .toLevel(request.getToLevel())
                .fromProductId(fromProduct.getId())
                .toProductId(toProduct.getId())
                .fromProductName(fromProduct.getName())
                .toProductName(toProduct.getName())
                .fromPrice(fromProduct.getPrice())
                .toPrice(toProduct.getPrice())
                .diffAmount(diffAmount)
                .status("PENDING")
                .effectiveLevel(request.getToLevel())
                .effectiveCoverage(coverageJson)
                .build();

        supplement = supplementOrderRepository.save(supplement);
        return convertToDTO(supplement);
    }

    @Override
    public SupplementOrderDTO getSupplementByNo(String supplementNo) {
        SupplementOrder supplement = supplementOrderRepository.findById(Long.valueOf(supplementNo))
                .orElseThrow(() -> new RuntimeException("补差单不存在"));
        return convertToDTO(supplement);
    }

    @Override
    public List<SupplementOrderDTO> getSupplementsByParentOrderId(Long parentOrderId) {
        List<SupplementOrder> supplements = supplementOrderRepository.findByParentOrderId(parentOrderId);
        return supplements.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean handleSupplementPaymentSuccess(String supplementNo) {
        SupplementOrder supplement = supplementOrderRepository.findAll().stream()
                .filter(s -> supplementNo.equals(s.getSupplementNo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("补差单不存在"));

        if (!"PENDING".equals(supplement.getStatus())) {
            return true;
        }

        supplement.setStatus("PAID");
        supplement.setPaidAt(java.time.LocalDateTime.now());
        supplementOrderRepository.save(supplement);

        return true;
    }

    private String detectCurrentInsuranceLevel(List<OrderItem> items) {
        List<Product> insuranceProducts = productRepository.findByTypeAndStatus(ProductType.INSURANCE, "ACTIVE");

        for (OrderItem item : items) {
            Optional<Product> matched = insuranceProducts.stream()
                    .filter(p -> p.getId().equals(item.getProductId()))
                    .findFirst();
            if (matched.isPresent()) {
                return matched.get().getLevel();
            }
        }

        for (OrderItem item : items) {
            if (item.getProductName() != null && item.getProductName().contains("基础版")) {
                return "BASIC";
            }
            if (item.getProductName() != null && item.getProductName().contains("标准版")) {
                return "STANDARD";
            }
            if (item.getProductName() != null && item.getProductName().contains("尊享版")) {
                return "PREMIUM";
            }
        }

        return null;
    }

    private BigDecimal findInsurancePrice(List<OrderItem> items, List<Product> insuranceProducts) {
        for (OrderItem item : items) {
            Optional<Product> matched = insuranceProducts.stream()
                    .filter(p -> p.getId().equals(item.getProductId()))
                    .findFirst();
            if (matched.isPresent()) {
                return matched.get().getPrice();
            }
        }
        return null;
    }

    private BigDecimal findInsurancePriceByLevel(String level, List<Product> insuranceProducts) {
        return insuranceProducts.stream()
                .filter(p -> level.equals(p.getLevel()))
                .map(Product::getPrice)
                .findFirst()
                .orElse(null);
    }

    private int compareLevel(String level1, String level2) {
        int rank1 = getLevelRank(level1);
        int rank2 = getLevelRank(level2);
        return Integer.compare(rank1, rank2);
    }

    private int getLevelRank(String level) {
        return switch (level) {
            case "BASIC" -> 1;
            case "STANDARD" -> 2;
            case "PREMIUM" -> 3;
            default -> 0;
        };
    }

    private String generateSupplementNo() {
        return "SP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String buildCoverageJson(String level) {
        Map<String, BigDecimal> coverage = COVERAGE_MAP.get(level);
        if (coverage == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, BigDecimal> entry : coverage.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private SupplementOrderDTO convertToDTO(SupplementOrder supplement) {
        SupplementOrderDTO dto = new SupplementOrderDTO();
        BeanUtils.copyProperties(supplement, dto);
        return dto;
    }
}

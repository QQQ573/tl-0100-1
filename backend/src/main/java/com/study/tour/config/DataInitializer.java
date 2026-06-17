package com.study.tour.config;

import com.study.tour.entity.Product;
import com.study.tour.enums.ProductLevel;
import com.study.tour.enums.ProductType;
import com.study.tour.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Product insuranceBasic = Product.builder()
                .name("基础版研学出行险")
                .description("涵盖基本意外保障，适合预算有限的学员。包含意外身故/伤残20万，意外医疗2万。")
                .price(new BigDecimal("29.90"))
                .type(ProductType.INSURANCE.name())
                .level(ProductLevel.BASIC.name())
                .status("ACTIVE")
                .build();
        productRepository.save(insuranceBasic);

        Product insuranceStandard = Product.builder()
                .name("标准版研学出行险")
                .description("全面保障方案，包含意外身故/伤残50万，意外医疗5万，紧急救援10万。")
                .price(new BigDecimal("59.90"))
                .type(ProductType.INSURANCE.name())
                .level(ProductLevel.STANDARD.name())
                .status("ACTIVE")
                .build();
        productRepository.save(insuranceStandard);

        Product insurancePremium = Product.builder()
                .name("尊享版研学出行险")
                .description("高端保障方案，包含意外身故/伤残100万，意外医疗10万，紧急救援20万，行程取消保障。")
                .price(new BigDecimal("99.90"))
                .type(ProductType.INSURANCE.name())
                .level(ProductLevel.PREMIUM.name())
                .status("ACTIVE")
                .build();
        productRepository.save(insurancePremium);

        Product agreementBasic = Product.builder()
                .name("基础版电子协议")
                .description("标准研学服务协议，包含基本服务条款和安全责任约定。")
                .price(new BigDecimal("0.00"))
                .type(ProductType.AGREEMENT.name())
                .level(ProductLevel.BASIC.name())
                .status("ACTIVE")
                .build();
        productRepository.save(agreementBasic);

        Product agreementPremium = Product.builder()
                .name("尊享版电子协议")
                .description("定制化研学服务协议，包含个性化服务条款、专属辅导员配置等增值服务。")
                .price(new BigDecimal("199.00"))
                .type(ProductType.AGREEMENT.name())
                .level(ProductLevel.PREMIUM.name())
                .status("ACTIVE")
                .build();
        productRepository.save(agreementPremium);

        Product packageBasic = Product.builder()
                .name("基础组合包（险+协议）")
                .description("基础版研学出行险 + 基础版电子协议，超值组合优惠。")
                .price(new BigDecimal("19.90"))
                .type(ProductType.PACKAGE.name())
                .level(ProductLevel.BASIC.name())
                .status("ACTIVE")
                .build();
        productRepository.save(packageBasic);

        Product packageStandard = Product.builder()
                .name("标准组合包（险+协议）")
                .description("标准版研学出行险 + 基础版电子协议，性价比之选。")
                .price(new BigDecimal("49.90"))
                .type(ProductType.PACKAGE.name())
                .level(ProductLevel.STANDARD.name())
                .status("ACTIVE")
                .build();
        productRepository.save(packageStandard);

        Product packagePremium = Product.builder()
                .name("尊享组合包（险+协议）")
                .description("尊享版研学出行险 + 尊享版电子协议，一站式高端研学保障。")
                .price(new BigDecimal("249.00"))
                .type(ProductType.PACKAGE.name())
                .level(ProductLevel.PREMIUM.name())
                .status("ACTIVE")
                .build();
        productRepository.save(packagePremium);
    }
}

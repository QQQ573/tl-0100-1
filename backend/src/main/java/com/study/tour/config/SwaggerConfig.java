package com.study.tour.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("暑期研学出行险+电子协议组合包 API")
                        .version("1.0.0")
                        .description("暑期研学出行险与电子协议组合包服务接口文档")
                        .contact(new Contact()
                                .name("研学保险团队")
                                .email("support@study-tour.com")));
    }
}

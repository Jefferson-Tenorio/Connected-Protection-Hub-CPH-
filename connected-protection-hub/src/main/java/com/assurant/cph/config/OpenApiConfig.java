package com.assurant.cph.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI connectedProtectionHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Connected Protection Hub API")
                        .description("Modular platform for protection, insurance and assistance management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("CPH Team")
                                .email("cph-support@assurant.com")));
    }
}
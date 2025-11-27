package com.assurant.cph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ConnectedProtectionHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConnectedProtectionHubApplication.class, args);
    }
}
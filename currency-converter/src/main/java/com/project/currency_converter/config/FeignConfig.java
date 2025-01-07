package com.project.currency_converter.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Request.Options options() {
        return new Request.Options(1000, 3000);
    }
}
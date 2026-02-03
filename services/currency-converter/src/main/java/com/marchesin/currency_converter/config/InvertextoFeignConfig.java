package com.marchesin.currency_converter.config;

import feign.Request;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvertextoFeignConfig {
    @Value("${token.api.invertexto}")
    private String invertextoTokenApi;

    @Bean
    public Request.Options invertextoOptions() {
        return new Request.Options(1000, 3000);
    }

    @Bean
    public RequestInterceptor invertextoInterceptor() {
        return template ->
                template.query("token", invertextoTokenApi);
    }
}
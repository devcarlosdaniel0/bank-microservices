package com.marchesin.currency_converter.config;

import feign.Request;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FxRatesFeignConfig {
    @Value("${token.api.fxrates}")
    private String fxratesTokenApi;

    @Bean
    public Request.Options fxratesOption() {
        return new Request.Options(1000, 3000);
    }

    @Bean
    public RequestInterceptor fxRatesInterceptor() {
        return template ->
                template.header("Authorization", "Bearer " + fxratesTokenApi);
    }
}
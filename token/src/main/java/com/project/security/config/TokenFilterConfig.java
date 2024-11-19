package com.project.security.config;

import com.project.security.filter.JwtAuthenticationFilter;
import com.project.security.service.TokenServiceGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TokenFilterConfig {

    private final TokenServiceGeneral tokenService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenService);
    }
}
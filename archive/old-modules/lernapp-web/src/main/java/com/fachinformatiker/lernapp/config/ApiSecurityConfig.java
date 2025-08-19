package com.fachinformatiker.lernapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * API Security Config - Öffentlicher Zugang für Demo
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class ApiSecurityConfig {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Alle API-Endpoints öffentlich für Demo
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/api/learning-engine/**").permitAll()
                .requestMatchers("/api/questions/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/api/topics/**").permitAll()
                .requestMatchers("/api/demo/**").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
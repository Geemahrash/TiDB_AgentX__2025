package com.backend.AI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf().disable() // disable csrf for simplicity
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // allow all endpoints without login
                );
        return http.build();
    }
}

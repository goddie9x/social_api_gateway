package com.godsocial.ApiGateway.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.godsocial.ApiGateway.filters.JwtAuthenticationFilter;

@EnableWebFluxSecurity
@Configuration
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private AppConfigProperties appConfigProperties;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(appConfigProperties.getExcludedPaths().toArray(new String[0])).permitAll() 
                .anyExchange().authenticated() 
            )
            .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION) 
            .csrf(csrf -> csrf.disable())
            .build();
    }
}

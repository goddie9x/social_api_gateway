package com.godsocial.ApiGateway.filters;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godsocial.ApiGateway.models.AuthInfo;
import com.godsocial.ApiGateway.services.JwtUtil;

import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    ObjectMapper objectMapper;

    private static final List<String> EXCLUDED_PATHS = List.of(
        "/api/v1/users/login",
        "/api/v1/users/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        
        if (EXCLUDED_PATHS.contains(path)) {
            return chain.filter(exchange);
        }
        System.out.println("JwtAuthenticationFilter called for path: " + path);
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                AuthInfo currentUser = jwtUtil.extractAuthInfo(token);

                if (currentUser != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String currentUserJson = objectMapper.writeValueAsString(currentUser);

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(r -> r.headers(headers -> {
                                headers.set("X-Current-User", currentUserJson);
                            }))
                            .build();

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            currentUser, null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + currentUser.getRole())));

                    SecurityContext context = new SecurityContextImpl(authentication);
                    return chain.filter(mutatedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                }
            } catch (Exception e) {
                return Mono.error(e);
            }
        }

        return chain.filter(exchange);
    }
}

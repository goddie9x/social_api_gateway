package com.godsocial.ApiGateway.filters;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.godsocial.ApiGateway.configs.AppConfigProperties;
import com.godsocial.ApiGateway.models.AuthInfo;
import com.godsocial.ApiGateway.services.JwtUtil;
import com.godsocial.ApiGateway.services.RedisService;

import io.jsonwebtoken.ExpiredJwtException;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisService redisService;
    @Autowired
    private AppConfigProperties appConfigProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (appConfigProperties.getExcludedPaths().contains(path)) {
            return chain.filter(exchange);
        }
        System.out.println("JwtAuthenticationFilter called for path: " + path);
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                AuthInfo currentUser = jwtUtil.extractAuthInfo(token);
                String tokenExist = redisService.getValue(currentUser.getUserId());
                if (tokenExist != null && tokenExist.equals(token) && currentUser != null) {
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
            } catch (ExpiredJwtException e) {
                return handleJwtError(exchange, "JWT token has expired", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return Mono.error(e);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> handleJwtError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = String.format("{\"error\": \"%s\"}", message);

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(errorResponse.getBytes())));
    }
}

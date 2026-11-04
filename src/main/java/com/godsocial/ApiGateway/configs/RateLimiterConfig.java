package com.godsocial.ApiGateway.configs;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Rate limiting for the API gateway using Redis.
 * Prevents abuse by limiting requests per client.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Default rate limiter: 100 requests/second sustained, 200 burst.
     * Adjust via application.yml if needed.
     */
    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(100, 200);
    }

    /**
     * Strict rate limiter for auth endpoints: 10 requests/second, 20 burst.
     * Protects login/register from brute force.
     */
    @Bean
    public RedisRateLimiter authRateLimiter() {
        return new RedisRateLimiter(10, 20);
    }

    /**
     * Resolve the rate limit key by client IP.
     * For authenticated routes, could be extended to use user ID from JWT.
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown"
        );
    }
}

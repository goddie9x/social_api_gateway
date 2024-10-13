package com.godsocial.ApiGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import com.godsocial.ApiGateway.configs.AppConfigProperties;

@SpringBootApplication
@EnableRedisRepositories
@EnableConfigurationProperties(AppConfigProperties.class)
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}

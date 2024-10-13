package com.godsocial.ApiGateway.configs;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {
    private List<String> excludedPaths;

    public List<String> getExcludedPaths() {
        return excludedPaths;
    }

    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }
}

package com.github.fanzezhen.demo.fun.ai.mcp.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Demo 业务服务器配置属性
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Validated
@ConfigurationProperties(prefix = "demo.business")
public record DemoBusinessServerProperties(
        @NotBlank String baseUrl,
        Duration connectTimeout,
        Duration readTimeout,
        @Min(1) @Max(10) int candidateLimit,
        @Min(1) @Max(200) int defaultPageSize,
        @Min(1) @Max(200) int maxPageSize
) {

    public DemoBusinessServerProperties {
        connectTimeout = connectTimeout == null ? Duration.ofSeconds(5) : connectTimeout;
        readTimeout = readTimeout == null ? Duration.ofSeconds(20) : readTimeout;
        candidateLimit = candidateLimit <= 0 ? 5 : candidateLimit;
        defaultPageSize = defaultPageSize <= 0 ? 20 : defaultPageSize;
        maxPageSize = maxPageSize <= 0 ? 100 : maxPageSize;
    }
}

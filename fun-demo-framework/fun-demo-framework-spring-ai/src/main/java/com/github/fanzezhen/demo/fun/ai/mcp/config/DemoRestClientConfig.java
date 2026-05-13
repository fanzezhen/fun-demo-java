package com.github.fanzezhen.demo.fun.ai.mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Demo RestClient 配置
 * 配置用于调用外部企业信息 API 的 HTTP 客户端
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Configuration
public class DemoRestClientConfig {

    /**
     * 创建 Demo RestClient Bean
     * 配置连接超时、读取超时和基础 URL
     */
    @Bean
    RestClient demoRestClient(DemoBusinessServerProperties properties) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) properties.connectTimeout().toMillis());
        requestFactory.setReadTimeout((int) properties.readTimeout().toMillis());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}

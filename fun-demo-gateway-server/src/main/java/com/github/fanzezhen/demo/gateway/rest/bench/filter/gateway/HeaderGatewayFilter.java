package com.github.fanzezhen.demo.gateway.rest.bench.filter.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求头网关过滤器
 *
 * @author fanzezhen
 * @createTime 2024/2/7 17:50
 * @since 3
 */
@Component
public class HeaderGatewayFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 在这里可以添加自定义逻辑，例如添加响应头
        exchange.getResponse().getHeaders().add("X-Response-Custom-Header", "CustomValue");

        // 继续执行过滤器链
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 定义过滤器的执行顺序，值越小，优先级越高
        return 1;
    }
}
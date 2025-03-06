package com.github.fanzezhen.demo.gateway.rest.bench.filter.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 日志全局过滤器
 *
 * @author fanzezhen
 * @createTime 2024/2/7 17:48
 * @since 3
 */
@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 在这里可以添加自定义逻辑，例如记录请求信息
        log.info("Request URI: " + exchange.getRequest().getURI().getRawPath());

        // 继续执行过滤器链
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 定义过滤器的执行顺序，值越小，优先级越高
        return -1;
    }
}
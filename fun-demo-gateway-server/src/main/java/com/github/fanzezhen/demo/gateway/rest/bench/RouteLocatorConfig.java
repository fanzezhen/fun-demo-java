package com.github.fanzezhen.demo.gateway.rest.bench;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义网关路由配置
 *
 * @author zezhen.fan
 */
@Configuration
public class RouteLocatorConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocator) {
        RouteLocatorBuilder.Builder routes = routeLocator.routes();
        //说明：访问 localhost:9527/news 地址会转发到https://www.anyknew.com/地址
        //lb: 请求地址中必须包含lb.filters(f->f.stripPrefix(1)) //去掉第一个路径.uri("lb://cloud-payment-service") //lb: 负载均衡指向地址)
        return routes
                .route("path_route1", r -> r.path("/news").uri("https://www.anyknew.com/"))
                .route(p -> p.path("/lb/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb:/")
                )
                .build();
    }
}

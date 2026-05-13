package com.github.fanzezhen.demo.fun.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring AI MCP Demo 应用启动类
 * <p>
 * 使用三个 Bean 配置模式：
 * <ul>
 *   <li>WebMvcStreamableServerTransportProvider - HTTP 传输层</li>
 *   <li>RouterFunction - 路由层</li>
 *   <li>McpSyncServer - MCP 服务器</li>
 * </ul>
 * <p>
 * 使用 @ConfigurationPropertiesScan 扫描所有配置属性类：
 * <ul>
 *   <li>DemoMcpServersProperties - MCP 服务器配置</li>
 *   <li>DemoBusinessServerProperties - 业务服务器配置</li>
 * </ul>
 *
 * @author fanzezhen
 */
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.github.fanzezhen.demo.fun.ai")
public class FrameworkAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkAiApplication.class, args);
    }

}

package com.github.fanzezhen.demo.fun.ai.mcp.config;

import com.github.fanzezhen.demo.fun.ai.mcp.tool.DemoBasicTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demo Basic MCP 工具配置
 * 配置基础工具的回调提供者
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Configuration
public class DemoBasicMcpConfig {

    /**
     * 配置 Demo Basic 工具回调提供者
     * 将 DemoBasicTools 中的所有 @Tool 方法注册为可调用的工具
     */
    @Bean
    public ToolCallbackProvider demoBasicToolCallbackProvider(DemoBasicTools demoBasicTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(demoBasicTools)
                .build();
    }
}

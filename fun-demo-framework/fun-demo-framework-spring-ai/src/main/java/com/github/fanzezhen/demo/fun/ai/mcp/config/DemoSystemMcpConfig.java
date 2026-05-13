package com.github.fanzezhen.demo.fun.ai.mcp.config;

import com.github.fanzezhen.demo.fun.ai.mcp.tool.DemoSystemTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demo System MCP 工具配置
 * 配置系统工具的回调提供者
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Configuration
public class DemoSystemMcpConfig {

    /**
     * 配置 Demo System 工具回调提供者
     * 将 DemoSystemTools 中的所有 @Tool 方法注册为可调用的工具
     */
    @Bean
    public ToolCallbackProvider demoSystemToolCallbackProvider(DemoSystemTools demoSystemTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(demoSystemTools)
                .build();
    }
}

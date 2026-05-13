package com.github.fanzezhen.demo.fun.ai.mcp.config;

import com.github.fanzezhen.demo.fun.ai.mcp.tool.DemoEnterpriseTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demo Enterprise MCP 工具配置
 * 配置企业信息查询工具的回调提供者
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Configuration
public class DemoEnterpriseMcpConfig {

    /**
     * 配置 Demo Enterprise 工具回调提供者
     * 将 DemoEnterpriseTools 中的所有 @Tool 方法注册为可调用的工具
     */
    @Bean
    public ToolCallbackProvider demoEnterpriseToolCallbackProvider(DemoEnterpriseTools tools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tools)
                .build();
    }
}

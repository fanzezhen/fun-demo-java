package com.github.fanzezhen.demo.fun.ai.mcp.config;

import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import tools.jackson.databind.json.JsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.server.webmvc.transport.WebMvcStreamableServerTransportProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Arrays;
import java.util.List;

/**
 * 多 MCP 服务器配置
 * 使用三个 bean 模式：TransportProvider + RouterFunction + McpSyncServer
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Configuration
public class MultiDemoMcpServerConfig {

    // ==================== Demo Basic Tools ====================

    @Bean
    WebMvcStreamableServerTransportProvider demoBasicTransportProvider(
            DemoMcpServersProperties properties) {
        return transportProvider(properties.require("demo-basic"));
    }

    @Bean
    RouterFunction<ServerResponse> demoBasicRouterFunction(
            @Qualifier("demoBasicTransportProvider") WebMvcStreamableServerTransportProvider provider) {
        return provider.getRouterFunction();
    }

    @Bean
    McpSyncServer demoBasicMcpServer(
            @Qualifier("demoBasicTransportProvider") WebMvcStreamableServerTransportProvider provider,
            @Qualifier("demoBasicToolCallbackProvider") ToolCallbackProvider tools,
            DemoMcpServersProperties properties) {
        return mcpServer(provider, tools, properties, "demo-basic");
    }

    // ==================== Demo System Tools ====================

    @Bean
    WebMvcStreamableServerTransportProvider demoSystemTransportProvider(
            DemoMcpServersProperties properties) {
        return transportProvider(properties.require("demo-system"));
    }

    @Bean
    RouterFunction<ServerResponse> demoSystemRouterFunction(
            @Qualifier("demoSystemTransportProvider") WebMvcStreamableServerTransportProvider provider) {
        return provider.getRouterFunction();
    }

    @Bean
    McpSyncServer demoSystemMcpServer(
            @Qualifier("demoSystemTransportProvider") WebMvcStreamableServerTransportProvider provider,
            @Qualifier("demoSystemToolCallbackProvider") ToolCallbackProvider tools,
            DemoMcpServersProperties properties) {
        return mcpServer(provider, tools, properties, "demo-system");
    }

    // ==================== Demo Enterprise Tools ====================

    @Bean
    WebMvcStreamableServerTransportProvider demoEnterpriseTransportProvider(
            DemoMcpServersProperties properties) {
        return transportProvider(properties.require("demo-enterprise"));
    }

    @Bean
    RouterFunction<ServerResponse> demoEnterpriseRouterFunction(
            @Qualifier("demoEnterpriseTransportProvider") WebMvcStreamableServerTransportProvider provider) {
        return provider.getRouterFunction();
    }

    @Bean
    McpSyncServer demoEnterpriseMcpServer(
            @Qualifier("demoEnterpriseTransportProvider") WebMvcStreamableServerTransportProvider provider,
            @Qualifier("demoEnterpriseToolCallbackProvider") ToolCallbackProvider tools,
            DemoMcpServersProperties properties) {
        return mcpServer(provider, tools, properties, "demo-enterprise");
    }

    // ==================== Helper Methods ====================

    private WebMvcStreamableServerTransportProvider transportProvider(
            DemoMcpServersProperties.Server server) {
        JsonMapper jsonMapper = JsonMapper.builder().build();
        return WebMvcStreamableServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(jsonMapper))
                .mcpEndpoint(server.getEndpoint())
                .build();
    }

    private McpSyncServer mcpServer(WebMvcStreamableServerTransportProvider provider,
                                    ToolCallbackProvider toolCallbackProvider,
                                    DemoMcpServersProperties properties,
                                    String serverKey) {
        var server = properties.require(serverKey);
        List<ToolCallback> callbacks = Arrays.asList(toolCallbackProvider.getToolCallbacks());
        var tools = McpToolUtils.toSyncToolSpecification(callbacks);
        return McpServer.sync(provider)
                .serverInfo(server.getName(), properties.getVersion())
                .capabilities(McpSchema.ServerCapabilities.builder().tools(false).build())
                .tools(tools)
                .instructions(server.getInstructions())
                .requestTimeout(properties.getRequestTimeout())
                .immediateExecution(true)
                .build();
    }
}

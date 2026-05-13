package com.github.fanzezhen.demo.fun.ai.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Demo MCP 服务器配置属性
 * 通过 @ConfigurationPropertiesScan 自动扫描注册
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@ConfigurationProperties(prefix = "demo.mcp")
public class DemoMcpServersProperties {

    private String version = "1.0.0";

    private Duration requestTimeout = Duration.ofSeconds(60);

    private Map<String, Server> servers = new LinkedHashMap<>();

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Duration getRequestTimeout() {
        return this.requestTimeout;
    }

    public void setRequestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Map<String, Server> getServers() {
        return this.servers;
    }

    public void setServers(Map<String, Server> servers) {
        this.servers = servers;
    }

    public Server require(String key) {
        var server = this.servers.get(key);
        if (server == null) {
            throw new IllegalStateException("缺少 Demo MCP 服务器配置: " + key);
        }
        return server;
    }

    public static class Server {

        private String name;

        private String endpoint;

        private String instructions;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEndpoint() {
            return this.endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getInstructions() {
            return this.instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }
    }
}

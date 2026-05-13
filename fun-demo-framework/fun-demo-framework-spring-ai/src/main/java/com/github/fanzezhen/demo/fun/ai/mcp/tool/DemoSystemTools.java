package com.github.fanzezhen.demo.fun.ai.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Demo MCP 系统工具类
 * 提供系统信息查询功能
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Component
public class DemoSystemTools {

    @Tool(
        name = "demo_system_get_info",
        description = "获取系统基本信息"
    )
    public SystemInfo getSystemInfo() {
        return new SystemInfo(
            System.getProperty("java.version"),
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("user.name"),
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB"
        );
    }

    @Tool(
        name = "demo_system_get_memory",
        description = "获取 JVM 内存使用情况"
    )
    public MemoryInfo getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return new MemoryInfo(
            formatBytes(maxMemory),
            formatBytes(totalMemory),
            formatBytes(usedMemory),
            formatBytes(freeMemory),
            String.format("%.2f%%", (double) usedMemory / totalMemory * 100)
        );
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 系统信息
     */
    public record SystemInfo(
        String javaVersion,
        String osName,
        String osVersion,
        String userName,
        int availableProcessors,
        String maxMemory
    ) {}

    /**
     * 内存信息
     */
    public record MemoryInfo(
        String maxMemory,
        String totalMemory,
        String usedMemory,
        String freeMemory,
        String usagePercentage
    ) {}
}

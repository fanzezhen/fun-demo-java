package com.github.fanzezhen.demo.fun.ai.mcp.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DemoSystemTools 测试类
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
class DemoSystemToolsTest {

    private DemoSystemTools demoSystemTools;

    @BeforeEach
    void setUp() {
        demoSystemTools = new DemoSystemTools();
    }

    @Test
    void testGetSystemInfo() {
        DemoSystemTools.SystemInfo systemInfo = demoSystemTools.getSystemInfo();

        assertNotNull(systemInfo);
        assertNotNull(systemInfo.javaVersion());
        assertNotNull(systemInfo.osName());
        assertNotNull(systemInfo.osVersion());
        assertNotNull(systemInfo.userName());
        assertTrue(systemInfo.availableProcessors() > 0);
        assertNotNull(systemInfo.maxMemory());
    }

    @Test
    void testGetMemoryInfo() {
        DemoSystemTools.MemoryInfo memoryInfo = demoSystemTools.getMemoryInfo();

        assertNotNull(memoryInfo);
        assertNotNull(memoryInfo.maxMemory());
        assertNotNull(memoryInfo.totalMemory());
        assertNotNull(memoryInfo.usedMemory());
        assertNotNull(memoryInfo.freeMemory());
        assertNotNull(memoryInfo.usagePercentage());

        // 验证使用率百分比格式
        assertTrue(memoryInfo.usagePercentage().endsWith("%"));
    }

    @Test
    void testMemoryInfoFormat() {
        DemoSystemTools.MemoryInfo memoryInfo = demoSystemTools.getMemoryInfo();

        // 验证内存单位格式（应该包含 B, KB, MB 或 GB）
        assertTrue(memoryInfo.maxMemory().matches(".*\\s(B|KB|MB|GB)$"));
        assertTrue(memoryInfo.totalMemory().matches(".*\\s(B|KB|MB|GB)$"));
        assertTrue(memoryInfo.usedMemory().matches(".*\\s(B|KB|MB|GB)$"));
        assertTrue(memoryInfo.freeMemory().matches(".*\\s(B|KB|MB|GB)$"));
    }
}

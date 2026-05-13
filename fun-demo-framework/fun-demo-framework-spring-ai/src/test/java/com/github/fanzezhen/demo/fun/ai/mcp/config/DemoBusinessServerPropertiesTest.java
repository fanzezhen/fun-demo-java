package com.github.fanzezhen.demo.fun.ai.mcp.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DemoBusinessServerProperties 测试类
 * 测试 Record 的紧凑构造器默认值逻辑
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
class DemoBusinessServerPropertiesTest {

    @Test
    void testDefaultValues() {
        // 测试默认值逻辑（Record 的紧凑构造器）
        var properties = new DemoBusinessServerProperties(
                "https://test.com",
                null,  // 应该使用默认值 5s
                null,  // 应该使用默认值 20s
                0,     // 应该使用默认值 5
                0,     // 应该使用默认值 20
                0      // 应该使用默认值 100
        );

        assertEquals("https://test.com", properties.baseUrl());
        assertEquals(Duration.ofSeconds(5), properties.connectTimeout());
        assertEquals(Duration.ofSeconds(20), properties.readTimeout());
        assertEquals(5, properties.candidateLimit());
        assertEquals(20, properties.defaultPageSize());
        assertEquals(100, properties.maxPageSize());
    }

    @Test
    void testCustomValues() {
        var properties = new DemoBusinessServerProperties(
                "https://custom.com",
                Duration.ofSeconds(10),
                Duration.ofSeconds(30),
                8,
                50,
                150
        );

        assertEquals("https://custom.com", properties.baseUrl());
        assertEquals(Duration.ofSeconds(10), properties.connectTimeout());
        assertEquals(Duration.ofSeconds(30), properties.readTimeout());
        assertEquals(8, properties.candidateLimit());
        assertEquals(50, properties.defaultPageSize());
        assertEquals(150, properties.maxPageSize());
    }

    @Test
    void testNegativeValuesAreReplacedWithDefaults() {
        var properties = new DemoBusinessServerProperties(
                "https://negative.com",
                Duration.ofSeconds(1),
                Duration.ofSeconds(2),
                -1,    // 负数应该使用默认值 5
                -10,   // 负数应该使用默认值 20
                -5     // 负数应该使用默认值 100
        );

        assertEquals("https://negative.com", properties.baseUrl());
        assertEquals(Duration.ofSeconds(1), properties.connectTimeout());
        assertEquals(Duration.ofSeconds(2), properties.readTimeout());
        assertEquals(5, properties.candidateLimit());
        assertEquals(20, properties.defaultPageSize());
        assertEquals(100, properties.maxPageSize());
    }
}

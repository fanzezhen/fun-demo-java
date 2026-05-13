package com.github.fanzezhen.demo.fun.ai.mcp.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DemoBasicTools 测试类
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
class DemoBasicToolsTest {

    private DemoBasicTools demoBasicTools;

    @BeforeEach
    void setUp() {
        demoBasicTools = new DemoBasicTools();
    }

    @Test
    void testHello_withName() {
        String result = demoBasicTools.hello("张三");
        assertEquals("你好, 张三! 欢迎使用 MCP Demo 基础工具!", result);
    }

    @Test
    void testHello_withoutName() {
        String result = demoBasicTools.hello(null);
        assertEquals("你好,欢迎使用 MCP Demo 基础工具!", result);
    }

    @Test
    void testHello_withEmptyName() {
        String result = demoBasicTools.hello("");
        assertEquals("你好,欢迎使用 MCP Demo 基础工具!", result);
    }

    @Test
    void testCalculate_add() {
        DemoBasicTools.CalculationResult result = demoBasicTools.calculate(10.5, 5.3, "+");
        assertEquals("10.50 + 5.30", result.expression());
        assertEquals(15.8, result.result(), 0.01);
    }

    @Test
    void testCalculate_subtract() {
        DemoBasicTools.CalculationResult result = demoBasicTools.calculate(10, 3, "-");
        assertEquals("10.00 - 3.00", result.expression());
        assertEquals(7, result.result(), 0.01);
    }

    @Test
    void testCalculate_multiply() {
        DemoBasicTools.CalculationResult result = demoBasicTools.calculate(4, 5, "*");
        assertEquals("4.00 * 5.00", result.expression());
        assertEquals(20, result.result(), 0.01);
    }

    @Test
    void testCalculate_divide() {
        DemoBasicTools.CalculationResult result = demoBasicTools.calculate(10, 2, "/");
        assertEquals("10.00 / 2.00", result.expression());
        assertEquals(5, result.result(), 0.01);
    }

    @Test
    void testCalculate_divideByZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            demoBasicTools.calculate(10, 0, "/");
        });
    }

    @Test
    void testCalculate_unsupportedOperator() {
        assertThrows(IllegalArgumentException.class, () -> {
            demoBasicTools.calculate(10, 5, "%");
        });
    }
}

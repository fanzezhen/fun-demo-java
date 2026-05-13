package com.github.fanzezhen.demo.fun.ai.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Demo MCP 基础工具类
 * 提供基本的问候和计算功能
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Component
public class DemoBasicTools {

    @Tool(
        name = "demo_basic_hello",
        description = "简单的问候工具,返回问候语"
    )
    public String hello(
        @ToolParam(description = "要问候的名字") String name
    ) {
        if (name == null || name.trim().isEmpty()) {
            return "你好,欢迎使用 MCP Demo 基础工具!";
        }
        return String.format("你好, %s! 欢迎使用 MCP Demo 基础工具!", name);
    }

    @Tool(
        name = "demo_basic_calculate",
        description = "简单的计算工具,支持加减乘除"
    )
    public CalculationResult calculate(
        @ToolParam(description = "第一个数字") double num1,
        @ToolParam(description = "第二个数字") double num2,
        @ToolParam(description = "运算符: +, -, *, /") String operator
    ) {
        double result;
        String expression;

        switch (operator) {
            case "+":
                result = num1 + num2;
                expression = String.format("%.2f + %.2f", num1, num2);
                break;
            case "-":
                result = num1 - num2;
                expression = String.format("%.2f - %.2f", num1, num2);
                break;
            case "*":
                result = num1 * num2;
                expression = String.format("%.2f * %.2f", num1, num2);
                break;
            case "/":
                if (num2 == 0) {
                    throw new IllegalArgumentException("除数不能为零");
                }
                result = num1 / num2;
                expression = String.format("%.2f / %.2f", num1, num2);
                break;
            default:
                throw new IllegalArgumentException("不支持的运算符: " + operator);
        }

        return new CalculationResult(expression, result);
    }

    /**
     * 计算结果
     */
    public record CalculationResult(
        String expression,
        double result
    ) {}
}

package com.github.fanzezhen.demo.fun.ai.mcp.tool.command;

/**
 * 企业基本信息查询命令
 *
 * @author fanzezhen
 * @since 2026-05-13
 */
public record EnterpriseBasicInfoCommand(
        String keyword,
        String apiKey
) {
}

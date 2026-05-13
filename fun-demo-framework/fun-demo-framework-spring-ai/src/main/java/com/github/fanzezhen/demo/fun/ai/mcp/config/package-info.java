/**
 * MCP 配置包
 * <p>
 * <b>为什么每个 MCP 服务器需要三个 Bean？</b>
 * <p>
 * MCP 服务器的完整配置包含三层结构：
 * <ol>
 *   <li><b>TransportProvider Bean</b> - 传输层提供者，负责 HTTP 请求/响应的序列化</li>
 *   <li><b>RouterFunction Bean</b> - 路由函数，将 HTTP 端点映射到传输层</li>
 *   <li><b>McpSyncServer Bean</b> - MCP 服务器实例，整合传输层、工具和配置</li>
 * </ol>
 * <p>
 * <b>本项目的配置方式</b>
 * <p>
 * 使用 Spring AI 2.0.0-M6 的自动配置机制：
 * <ul>
 *   <li>自动配置创建默认的 TransportProvider</li>
 *   <li>自动配置创建默认的 RouterFunction</li>
 *   <li>自动配置创建默认的 McpSyncServer</li>
 *   <li>只需注册 ToolCallbackProvider Bean 即可</li>
 * </ul>
 * <p>
 * <b>如何支持多个独立的 MCP 服务器？</b>
 * <p>
 * Spring AI 2.0 有两种方式：
 * <ol>
 *   <li><b>简单方式（本 demo 采用）</b>：所有工具在同一端点，通过不同的 ToolCallbackProvider 组织</li>
 *   <li><b>高级方式</b>：手动配置多个端点，需要禁用自动配置并手动创建三层 Bean</li>
 * </ol>
 * <p>
 * 本 demo 展示的是简单方式，更适合快速原型开发和中小型应用。
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
package com.github.fanzezhen.demo.fun.ai.mcp.config;

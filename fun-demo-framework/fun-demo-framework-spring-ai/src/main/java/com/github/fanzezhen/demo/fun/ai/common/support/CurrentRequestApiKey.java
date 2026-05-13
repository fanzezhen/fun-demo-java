package com.github.fanzezhen.demo.fun.ai.common.support;

import com.github.fanzezhen.demo.fun.ai.common.security.ApiKeyAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 当前请求 API Key 提取器
 * 从 Spring Security 上下文中提取 API Key 用于下游 API 调用
 *
 * @author fanzezhen
 * @since 2026-05-13
 */
@Component
public class CurrentRequestApiKey {

    /**
     * 获取当前请求的 API Key
     * 从 SecurityContext 中提取已认证的 API Key
     *
     * @return API Key
     * @throws IllegalStateException 如果当前请求未包含 API Key
     */
    public String require() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof ApiKeyAuthenticationToken token) {
            return token.getApiKey();
        }
        throw new IllegalStateException("Current request does not contain a propagated x-api-key.");
    }
}

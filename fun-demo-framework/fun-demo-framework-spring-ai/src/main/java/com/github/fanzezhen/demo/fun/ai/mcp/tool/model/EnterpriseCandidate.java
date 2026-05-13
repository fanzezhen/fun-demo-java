package com.github.fanzezhen.demo.fun.ai.mcp.tool.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 候选企业
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EnterpriseCandidate(
        @JsonProperty("企业名称")
        String name,

        @JsonProperty("统一社会信用代码")
        String creditCode,

        @JsonProperty("法定代表人")
        String legalRepresentative,

        @JsonProperty("经营状态")
        String businessStatus,

        @JsonProperty("注册地址")
        String registeredAddress,

        @JsonProperty("匹配度")
        Integer matchScore,

        @JsonProperty("匹配原因")
        String reason
) {
}

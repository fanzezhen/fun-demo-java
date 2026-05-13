package com.github.fanzezhen.demo.fun.ai.mcp.tool.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 企业查询结果
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EnterpriseQueryResult(
        @JsonProperty("关键词")
        String keyword,

        @JsonProperty("解析状态")
        boolean resolved,

        @JsonProperty("置信度")
        Double confidence,

        @JsonProperty("解析信息")
        String message,

        @JsonProperty("企业信息")
        EnterpriseInfo enterpriseInfo,

        @JsonProperty("候选企业")
        List<EnterpriseCandidate> candidates
) {

    /**
     * 创建成功解析的结果
     */
    public static EnterpriseQueryResult resolved(EnterpriseInfo info) {
        return new EnterpriseQueryResult(
                info != null ? info.name() : null,
                true,
                1.0,
                "企业唯一解析成功",
                info,
                null
        );
    }

    /**
     * 创建未解析的结果（有多个候选）
     */
    public static EnterpriseQueryResult unresolved(List<EnterpriseCandidate> candidates) {
        String keyword = candidates != null && !candidates.isEmpty() ? candidates.get(0).name() : null;
        return new EnterpriseQueryResult(
                keyword,
                false,
                null,
                "企业无法唯一解析，已返回候选企业，请补充更完整的企业名称后重试。",
                null,
                candidates
        );
    }

    /**
     * 创建未找到的结果
     */
    public static EnterpriseQueryResult notFound(String keyword) {
        return new EnterpriseQueryResult(
                keyword,
                false,
                0.0,
                "未找到匹配的企业：" + keyword,
                null,
                List.of()
        );
    }
}

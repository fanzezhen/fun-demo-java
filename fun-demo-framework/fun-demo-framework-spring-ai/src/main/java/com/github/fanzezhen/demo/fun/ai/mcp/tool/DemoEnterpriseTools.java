package com.github.fanzezhen.demo.fun.ai.mcp.tool;

import com.github.fanzezhen.demo.fun.ai.common.support.CurrentRequestApiKey;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.command.EnterpriseBasicInfoCommand;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.DemoEnterpriseToolResponse;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.EnterpriseQueryResult;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.service.DemoEnterpriseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Demo 企业信息查询工具
 * 提供企业基本信息查询和企业名称匹配功能
 * 支持多接口聚合查询
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Component
public class DemoEnterpriseTools {

    private static final Logger log = LoggerFactory.getLogger(DemoEnterpriseTools.class);

    private static final String KEYWORD_DESCRIPTION =
            "企业名称、统一社会信用代码、简称或历史名称。优先填写完整企业名称或统一社会信用代码；先解析唯一企业，歧义时返回候选企业并停止业务查询。";

    private final DemoEnterpriseService service;
    private final CurrentRequestApiKey currentRequestApiKey;

    public DemoEnterpriseTools(DemoEnterpriseService service,
                               CurrentRequestApiKey currentRequestApiKey) {
        this.service = service;
        this.currentRequestApiKey = currentRequestApiKey;
    }

    @Tool(
        name = "demo_enterprise_basic_information",
        description = "查询企业工商注册和主体身份信息。聚合企业搜索、详细信息等多个接口，一次性返回企业身份、资本信息、经营范围、行业分类等。适用于企业主体快速核验场景。"
    )
    public DemoEnterpriseToolResponse.BasicInfoToolResponse getEnterpriseInformation(
        @ToolParam(description = KEYWORD_DESCRIPTION) String keyword
    ) {
        if (!StringUtils.hasText(keyword)) {
            return new DemoEnterpriseToolResponse.BasicInfoToolResponse(
                    new DemoEnterpriseToolResponse.ResolutionResult("", false, 0.0, "关键词为空", null, null),
                    null, null, null, null, null, "关键词不能为空");
        }

        log.info("查询企业基本信息: keyword={}", keyword.trim());
        return service.basicInfo(new EnterpriseBasicInfoCommand(keyword, currentRequestApiKey.require()));
    }

    @Tool(
        name = "demo_enterprise_match_company",
        description = "识别并匹配企业主体。名称不完整、简称、历史名称或重名时先用它确认目标企业。"
    )
    public EnterpriseQueryResult matchCompany(
        @ToolParam(description = "企业关键词。优先填写完整企业名称或统一社会信用代码。") String keyword
    ) {
        if (!StringUtils.hasText(keyword)) {
            return EnterpriseQueryResult.notFound("");
        }

        log.info("匹配企业: keyword={}", keyword.trim());
        return service.searchEnterprise(keyword, currentRequestApiKey.require());
    }
}


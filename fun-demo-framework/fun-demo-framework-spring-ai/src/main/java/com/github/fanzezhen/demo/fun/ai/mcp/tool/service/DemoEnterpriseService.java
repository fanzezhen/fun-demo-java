package com.github.fanzezhen.demo.fun.ai.mcp.tool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fanzezhen.demo.fun.ai.mcp.client.DemoEnterpriseApi;
import com.github.fanzezhen.demo.fun.ai.mcp.client.DemoEnterpriseApiClient;
import com.github.fanzezhen.demo.fun.ai.mcp.config.DemoBusinessServerProperties;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.command.EnterpriseBasicInfoCommand;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.DemoEnterpriseToolResponse;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.EnterpriseCandidate;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.EnterpriseInfo;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.EnterpriseQueryResult;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Demo 企业信息服务
 * 处理企业信息查询的业务逻辑
 * 支持多接口聚合查询
 *
 * @author fanzezhen
 * @since 2026-05-13
 */
@Service
public class DemoEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(DemoEnterpriseService.class);

    private final DemoEnterpriseApiClient apiClient;
    private final DemoBusinessServerProperties properties;
    private final DemoEnterpriseMapper mapper;

    public DemoEnterpriseService(
            DemoEnterpriseApiClient apiClient,
            DemoBusinessServerProperties properties,
            DemoEnterpriseMapper mapper) {
        this.apiClient = apiClient;
        this.properties = properties;
        this.mapper = mapper;
    }

    /**
     * 查询企业基本信息（聚合多个接口）
     * 先搜索匹配企业，再并发调用多个接口获取详细信息
     */
    public DemoEnterpriseToolResponse.BasicInfoToolResponse basicInfo(EnterpriseBasicInfoCommand command) {
        long startedAt = System.currentTimeMillis();
        String normalizedKeyword = normalizeKeyword(command.keyword());

        // 步骤1: 先搜索匹配企业
        EnterpriseQueryResult searchResult = searchEnterprise(normalizedKeyword, command.apiKey());

        // 如果搜索未解析唯一企业，直接返回候选结果
        if (!searchResult.resolved()) {
            log.info("tool=demo_enterprise_basic_information keyword={} resolved=false candidates={}",
                    normalizedKeyword, searchResult.candidates() != null ? searchResult.candidates().size() : 0);
            return mapper.mapBasicInfo(searchResult, null, null, null, null);
        }

        // 步骤2: 已解析唯一企业，并发查询详细信息
        String eid = searchResult.enterpriseInfo().eid();

        // 并发调用多个接口
        var infoFuture = CompletableFuture.supplyAsync(
                () -> callApi(DemoEnterpriseApi.ENTERPRISE_INFO, eid, command.apiKey()));
        var contactFuture = CompletableFuture.supplyAsync(
                () -> callApi(DemoEnterpriseApi.ENTERPRISE_CONTACT_DETAIL, eid, command.apiKey()));
        var addressFuture = CompletableFuture.supplyAsync(
                () -> callApi(DemoEnterpriseApi.ENTERPRISE_ADDRESS_LIST, eid, command.apiKey()));
        var phoneFuture = CompletableFuture.supplyAsync(
                () -> callApi(DemoEnterpriseApi.ENTERPRISE_PHONE_LIST, eid, command.apiKey()));

        CompletableFuture.allOf(infoFuture, contactFuture, addressFuture, phoneFuture).join();

        var result = mapper.mapBasicInfo(
                searchResult,
                safeGetJson(infoFuture),
                safeGetJson(contactFuture),
                safeGetJson(addressFuture),
                safeGetJson(phoneFuture));
        log.info("tool=demo_enterprise_basic_information keyword={} eid={} resolved=true elapsedMs={}",
                normalizedKeyword, eid, System.currentTimeMillis() - startedAt);
        return result;
    }

    /**
     * 搜索企业（用于名称解析）
     */
    public EnterpriseQueryResult searchEnterprise(String keyword, String apiKey) {
        String normalizedKeyword = normalizeKeyword(keyword);

        try {
            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("keyword", normalizedKeyword);
            request.put("currentPage", 1);
            request.put("pageSize", properties.candidateLimit());

            // 调用搜索 API
            JsonNode response = apiClient.post(DemoEnterpriseApi.ENTERPRISE_SEARCH, request, apiKey);

            // 解析响应
            return parseSearchResponse(normalizedKeyword, response);

        } catch (ServiceException ex) {
            log.warn("企业搜索失败: keyword={}", normalizedKeyword, ex);
            return EnterpriseQueryResult.notFound(normalizedKeyword);
        } catch (Exception ex) {
            log.error("企业搜索异常: keyword={}", normalizedKeyword, ex);
            return EnterpriseQueryResult.notFound(normalizedKeyword);
        }
    }

    /**
     * 通用 API 调用方法（返回 JsonNode）
     */
    private JsonNode callApi(DemoEnterpriseApi api, String eid, String apiKey) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("eid", eid);
            return apiClient.post(api, request, apiKey);
        } catch (Exception ex) {
            log.error("API 调用异常: api={} eid={}", api.path(), eid, ex);
            return null;
        }
    }


    /**
     * 解析搜索响应
     */
    private EnterpriseQueryResult parseSearchResponse(String keyword, JsonNode response) {
        // API 返回的数据在 rows 字段中
        JsonNode rows = response.get("rows");
        if (rows == null || !rows.isArray() || rows.isEmpty()) {
            return EnterpriseQueryResult.notFound(keyword);
        }

        List<EnterpriseCandidate> candidates = new ArrayList<>();
        for (JsonNode item : rows) {
            EnterpriseCandidate candidate = new EnterpriseCandidate(
                    getText(item, "name"),
                    getText(item, "creditCode"),
                    getText(item, "operName"),              // 法定代表人字段名
                    getText(item, "businessStatus"),
                    getText(item, "regAddress"),            // 注册地址字段名
                    getInt(item, "score", 0),               // 匹配分数字段名
                    getText(item, "reason")
            );
            candidates.add(candidate);
        }

        // 如果只有一个候选且匹配度高，视为唯一解析
        if (candidates.size() == 1 || (candidates.size() > 0 && candidates.get(0).matchScore() >= 95)) {
            EnterpriseCandidate top = candidates.get(0);
            EnterpriseInfo info = new EnterpriseInfo(
                    getText(rows.get(0), "id"),             // eid 字段名
                    top.name(),                             // name
                    top.creditCode(),                       // creditCode
                    top.legalRepresentative(),              // legalRepresentative
                    null,                                   // registeredCapital
                    getText(rows.get(0), "foundDate"),      // foundedAt
                    top.businessStatus(),                   // businessStatus
                    getText(rows.get(0), "operType"),       // companyType
                    top.registeredAddress(),                // registeredAddress
                    null,                                   // businessScope
                    null,                                   // registrationAuthority
                    null,                                   // approvedAt
                    null,                                   // insuredEmployeeCount
                    null,                                   // industry
                    getText(rows.get(0), "phone"),          // phone
                    getText(rows.get(0), "email"),          // email
                    null                                    // website
            );
            return EnterpriseQueryResult.resolved(info);
        }

        // 多个候选，未唯一解析
        return EnterpriseQueryResult.unresolved(candidates);
    }

    /**
     * 规范化关键词
     */
    private String normalizeKeyword(String keyword) {
        return keyword != null ? keyword.trim() : "";
    }

    /**
     * 安全获取 JsonNode 结果
     */
    private JsonNode safeGetJson(CompletableFuture<JsonNode> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            log.error("异步查询失败", e);
            return null;
        }
    }

    /**
     * 获取 JSON 文本字段
     */
    private String getText(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode field = node.get(fieldName);
        return field.isNull() ? null : field.asText();
    }

    /**
     * 获取 JSON 整数字段
     */
    private int getInt(JsonNode node, String fieldName, int defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }
        JsonNode field = node.get(fieldName);
        return field.isNull() ? defaultValue : field.asInt(defaultValue);
    }
}

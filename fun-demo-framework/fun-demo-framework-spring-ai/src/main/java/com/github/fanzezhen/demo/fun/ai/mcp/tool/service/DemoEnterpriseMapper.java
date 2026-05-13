package com.github.fanzezhen.demo.fun.ai.mcp.tool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.DemoEnterpriseToolResponse;
import com.github.fanzezhen.demo.fun.ai.mcp.tool.model.EnterpriseQueryResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo 企业信息映射器
 * 负责将 API 响应映射为工具响应模型
 *
 * @author fanzezhen
 * @since 2026-05-13
 */
@Component
public class DemoEnterpriseMapper {

    private static final String UNRESOLVED_HINT = "企业无法唯一解析，已返回候选企业，请补充更完整的企业名称后重试。";

    /**
     * 映射企业基本信息
     * 聚合多个接口的响应数据，转换为统一的工具响应格式
     */
    public DemoEnterpriseToolResponse.BasicInfoToolResponse mapBasicInfo(
            EnterpriseQueryResult searchResult,
            JsonNode infoPayload,
            JsonNode contactPayload,
            JsonNode addressPayload,
            JsonNode phonePayload) {

        // 如果未解析唯一企业，返回解析结果
        if (!searchResult.resolved()) {
            return new DemoEnterpriseToolResponse.BasicInfoToolResponse(
                    toResolution(searchResult),
                    null, null, null, null, null, UNRESOLVED_HINT);
        }

        // 已解析唯一企业，映射详细信息
        return new DemoEnterpriseToolResponse.BasicInfoToolResponse(
                toResolution(searchResult),
                mapCompanyIdentity(infoPayload),
                mapCapitalInfo(infoPayload),
                mapBusinessTermInfo(infoPayload),
                mapIndustryInfo(infoPayload),
                mapContactInfo(infoPayload, contactPayload, addressPayload, phonePayload),
                null);
    }

    /**
     * 转换为解析结果
     */
    public DemoEnterpriseToolResponse.ResolutionResult toResolution(EnterpriseQueryResult result) {
        return new DemoEnterpriseToolResponse.ResolutionResult(
                result.keyword(),
                result.resolved(),
                result.confidence(),
                result.message(),
                result.enterpriseInfo(),
                result.candidates()
        );
    }

    /**
     * 映射企业身份信息
     */
    private DemoEnterpriseToolResponse.CompanyIdentity mapCompanyIdentity(JsonNode payload) {
        if (payload == null) {
            return null;
        }
        return new DemoEnterpriseToolResponse.CompanyIdentity(
                getText(payload, "eid"),
                getText(payload, "name"),
                firstNonEmpty(
                        getText(payload, "creditNo"),
                        getText(payload, "creditCode"),
                        getText(payload, "uscc")),
                getText(payload, "orgNo"),
                getText(payload, "regNo"),
                getText(payload, "businessStatus"),
                getText(payload, "companyType"),
                getText(payload, "foundedAt"),
                getText(payload, "legalRepresentative"),
                getText(payload, "legalRepresentativeId"),
                getText(payload, "legalRepresentativeTitle"),
                getText(payload, "latestAddress"),
                getText(payload, "registeredAddress"),
                getText(payload, "registrationAuthority"),
                getText(payload, "approvalDate"),
                getText(payload, "cancelledDate")
        );
    }

    /**
     * 映射资本信息
     */
    private DemoEnterpriseToolResponse.CapitalInfo mapCapitalInfo(JsonNode payload) {
        if (payload == null) {
            return null;
        }
        return new DemoEnterpriseToolResponse.CapitalInfo(
                getText(payload, "registeredCapital"),
                getText(payload, "registeredCapitalCurrency"),
                getText(payload, "paidInCapital"),
                getText(payload, "paidInCapitalCurrency"),
                getInteger(payload, "insuredEmployeeCount")
        );
    }

    /**
     * 映射经营期限信息
     */
    private DemoEnterpriseToolResponse.BusinessTermInfo mapBusinessTermInfo(JsonNode payload) {
        if (payload == null) {
            return null;
        }
        return new DemoEnterpriseToolResponse.BusinessTermInfo(
                getText(payload, "businessTermStart"),
                getText(payload, "businessTermEnd"),
                getText(payload, "businessScope")
        );
    }

    /**
     * 映射行业分类信息
     */
    private DemoEnterpriseToolResponse.IndustryInfo mapIndustryInfo(JsonNode payload) {
        if (payload == null) {
            return null;
        }
        return new DemoEnterpriseToolResponse.IndustryInfo(
                getText(payload, "industry"),
                getText(payload, "industryCode"),
                getText(payload, "nationalEconomyIndustry")
        );
    }

    /**
     * 映射联系方式信息
     * 从多个接口聚合企业的联系方式信息（邮箱、网址、地址、电话）
     */
    private DemoEnterpriseToolResponse.ContactInfo mapContactInfo(
            JsonNode infoPayload,
            JsonNode contactPayload,
            JsonNode addressPayload,
            JsonNode phonePayload) {

        // 从 contactPayload 提取联系信息
        List<String> phones = extractPhoneList(contactPayload, phonePayload);
        List<String> addresses = extractAddressList(contactPayload, addressPayload);
        List<String> emails = extractEmailList(contactPayload);
        List<String> websites = extractWebsiteList(contactPayload);
        String postcode = getText(contactPayload, "postcode");

        // 如果所有字段都为空，返回 null
        if (isEmpty(phones) && isEmpty(addresses) && isEmpty(emails) &&
            isEmpty(websites) && postcode == null) {
            return null;
        }

        return new DemoEnterpriseToolResponse.ContactInfo(
                phones, addresses, emails, websites, postcode);
    }

    /**
     * 提取电话列表（从 contactPayload 和 phonePayload 去重合并）
     */
    private List<String> extractPhoneList(JsonNode contactPayload, JsonNode phonePayload) {
        List<String> result = new java.util.ArrayList<>();

        // 从 contactPayload.phoneList 提取
        if (contactPayload != null && contactPayload.has("phoneList")) {
            JsonNode phoneList = contactPayload.get("phoneList");
            if (phoneList.isArray()) {
                for (JsonNode node : phoneList) {
                    String phone = getText(node, "phone");
                    if (phone != null && !phone.trim().isEmpty() && !result.contains(phone)) {
                        result.add(phone);
                    }
                }
            }
        }

        // 从 phonePayload 提取（去重）
        if (phonePayload != null && phonePayload.isArray()) {
            for (JsonNode node : phonePayload) {
                String phone = getText(node, "phone");
                if (phone != null && !phone.trim().isEmpty() && !result.contains(phone)) {
                    result.add(phone);
                }
            }
        }

        return result.isEmpty() ? null : result;
    }

    /**
     * 提取地址列表（从 contactPayload 和 addressPayload 去重合并）
     */
    private List<String> extractAddressList(JsonNode contactPayload, JsonNode addressPayload) {
        List<String> result = new java.util.ArrayList<>();

        // 从 contactPayload.addressList 提取
        if (contactPayload != null && contactPayload.has("addressList")) {
            JsonNode addressList = contactPayload.get("addressList");
            if (addressList.isArray()) {
                for (JsonNode node : addressList) {
                    String address = getText(node, "address");
                    if (address != null && !address.trim().isEmpty() && !result.contains(address)) {
                        result.add(address);
                    }
                }
            }
        }

        // 从 addressPayload 提取（去重）
        if (addressPayload != null && addressPayload.isArray()) {
            for (JsonNode node : addressPayload) {
                String address = getText(node, "address");
                if (address != null && !address.trim().isEmpty() && !result.contains(address)) {
                    result.add(address);
                }
            }
        }

        return result.isEmpty() ? null : result;
    }

    /**
     * 提取邮箱列表
     */
    private List<String> extractEmailList(JsonNode contactPayload) {
        if (contactPayload == null || !contactPayload.has("emailList")) {
            return null;
        }

        JsonNode emailList = contactPayload.get("emailList");
        if (!emailList.isArray() || emailList.isEmpty()) {
            return null;
        }

        List<String> result = new java.util.ArrayList<>();
        for (JsonNode node : emailList) {
            String email = getText(node, "value");
            if (email != null && !email.trim().isEmpty()) {
                result.add(email);
            }
        }

        return result.isEmpty() ? null : result;
    }

    /**
     * 提取网站列表
     */
    private List<String> extractWebsiteList(JsonNode contactPayload) {
        if (contactPayload == null || !contactPayload.has("websiteList")) {
            return null;
        }

        JsonNode websiteList = contactPayload.get("websiteList");
        if (!websiteList.isArray() || websiteList.isEmpty()) {
            return null;
        }

        List<String> result = new java.util.ArrayList<>();
        for (JsonNode node : websiteList) {
            if (!node.isNull()) {
                String website = node.asText();
                if (website != null && !website.trim().isEmpty()) {
                    result.add(website);
                }
            }
        }

        return result.isEmpty() ? null : result;
    }

    /**
     * 判断列表是否为空
     */
    private boolean isEmpty(List<String> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 获取第一个非空值
     */
    private String firstNonEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
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
    private Integer getInteger(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode field = node.get(fieldName);
        return field.isNull() ? null : field.asInt();
    }

}

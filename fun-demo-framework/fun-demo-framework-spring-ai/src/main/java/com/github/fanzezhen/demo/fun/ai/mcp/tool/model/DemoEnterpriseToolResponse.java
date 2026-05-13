package com.github.fanzezhen.demo.fun.ai.mcp.tool.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Demo 企业工具响应模型
 * 使用 Record 类型定义不可变的响应结构
 *
 * @author fanzezhen
 * @since 2026-05-13
 */
public class DemoEnterpriseToolResponse {

    /**
     * 企业基本信息工具响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BasicInfoToolResponse(
            @JsonProperty("企业解析") ResolutionResult resolution,
            @JsonProperty("企业身份信息") CompanyIdentity companyIdentity,
            @JsonProperty("资本信息") CapitalInfo capitalInfo,
            @JsonProperty("经营期限信息") BusinessTermInfo businessTermInfo,
            @JsonProperty("行业分类信息") IndustryInfo industryInfo,
            @JsonProperty("联系方式") ContactInfo contactInfo,
            @JsonProperty("提示") String hint
    ) {
    }

    /**
     * 企业解析结果
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ResolutionResult(
            @JsonProperty("关键词") String keyword,
            @JsonProperty("解析状态") Boolean resolved,
            @JsonProperty("置信度") Double confidence,
            @JsonProperty("解析信息") String message,
            @JsonProperty("企业信息") EnterpriseInfo company,
            @JsonProperty("候选企业") List<EnterpriseCandidate> candidates
    ) {
    }

    /**
     * 企业身份信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CompanyIdentity(
            @JsonProperty("企业ID") String eid,
            @JsonProperty("企业名称") String name,
            @JsonProperty("统一社会信用代码") String creditCode,
            @JsonProperty("组织机构代码") String orgNo,
            @JsonProperty("工商注册号") String regNo,
            @JsonProperty("经营状态") String businessStatus,
            @JsonProperty("企业类型") String companyType,
            @JsonProperty("成立日期") String foundedAt,
            @JsonProperty("法定代表人") String legalRepresentative,
            @JsonProperty("法定代表人ID") String legalRepresentativeId,
            @JsonProperty("法定代表人职务") String legalRepresentativeTitle,
            @JsonProperty("最新地址") String latestAddress,
            @JsonProperty("注册地址") String registeredAddress,
            @JsonProperty("登记机关") String registrationAuthority,
            @JsonProperty("核准日期") String approvedAt,
            @JsonProperty("注销日期") String cancelledAt
    ) {
    }

    /**
     * 资本信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CapitalInfo(
            @JsonProperty("注册资本") String registeredCapital,
            @JsonProperty("注册资本币种") String registeredCapitalCurrency,
            @JsonProperty("实缴资本") String paidInCapital,
            @JsonProperty("实缴资本币种") String paidInCapitalCurrency,
            @JsonProperty("参保人数") Integer insuredEmployeeCount
    ) {
    }

    /**
     * 经营期限信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BusinessTermInfo(
            @JsonProperty("营业期限开始") String businessTermStart,
            @JsonProperty("营业期限结束") String businessTermEnd,
            @JsonProperty("经营范围") String businessScope
    ) {
    }

    /**
     * 行业分类信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record IndustryInfo(
            @JsonProperty("所属行业") String industry,
            @JsonProperty("行业代码") String industryCode,
            @JsonProperty("国民经济行业分类") String nationalEconomyIndustry
    ) {
    }

    /**
     * 联系方式信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ContactInfo(
            @JsonProperty("电话") List<String> phones,
            @JsonProperty("地址") List<String> addresses,
            @JsonProperty("邮箱") List<String> emails,
            @JsonProperty("网站") List<String> websites,
            @JsonProperty("邮编") String postcode
    ) {
    }
}

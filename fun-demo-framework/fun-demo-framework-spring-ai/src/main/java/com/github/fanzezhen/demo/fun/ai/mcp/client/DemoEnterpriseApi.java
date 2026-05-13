package com.github.fanzezhen.demo.fun.ai.mcp.client;

/**
 * Demo 企业信息 API 端点定义
 * 定义所有企业信息查询相关的 API 路径
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
public enum DemoEnterpriseApi {

    // ==================== 企业搜索与匹配 ====================
    /**
     * 企业搜索 - 用于企业名称解析和匹配
     */
    ENTERPRISE_SEARCH("/openapi/v1/enterprise/search/simple-search"),

    // ==================== 企业基本信息 ====================
    /**
     * 企业工商登记信息
     */
    ENTERPRISE_INFO("/openapi/v1/enterprise/basic/information"),

    /**
     * 企业联系方式详情
     */
    ENTERPRISE_CONTACT_DETAIL("/openapi/v1/enterprise/basic/contact-detail"),

    /**
     * 企业地址列表
     */
    ENTERPRISE_ADDRESS_LIST("/openapi/v1/enterprise/basic/get-address-list"),

    /**
     * 企业电话列表
     */
    ENTERPRISE_PHONE_LIST("/openapi/v1/enterprise/basic/get-phone-list");

    private final String path;

    DemoEnterpriseApi(String path) {
        this.path = path;
    }

    /**
     * 获取 API 路径
     */
    public String path() {
        return path;
    }

    /**
     * 获取 API 描述（用于日志）
     */
    public String description() {
        return switch (this) {
            case ENTERPRISE_SEARCH -> "企业搜索";
            case ENTERPRISE_INFO -> "企业工商登记信息";
            case ENTERPRISE_CONTACT_DETAIL -> "企业联系方式详情";
            case ENTERPRISE_ADDRESS_LIST -> "企业地址列表";
            case ENTERPRISE_PHONE_LIST -> "企业电话列表";
        };
    }
}

package com.github.fanzezhen.demo.fun.ai.mcp.tool.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业信息模型
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EnterpriseInfo(
        @JsonProperty("企业ID")
        String eid,

        @JsonProperty("企业名称")
        String name,

        @JsonProperty("统一社会信用代码")
        String creditCode,

        @JsonProperty("法定代表人")
        String legalRepresentative,

        @JsonProperty("注册资本")
        String registeredCapital,

        @JsonProperty("成立日期")
        String foundedAt,

        @JsonProperty("经营状态")
        String businessStatus,

        @JsonProperty("企业类型")
        String companyType,

        @JsonProperty("注册地址")
        String registeredAddress,

        @JsonProperty("经营范围")
        String businessScope,

        @JsonProperty("登记机关")
        String registrationAuthority,

        @JsonProperty("核准日期")
        String approvedAt,

        @JsonProperty("参保人数")
        Integer insuredEmployeeCount,

        @JsonProperty("所属行业")
        String industry,

        @JsonProperty("联系电话")
        String phone,

        @JsonProperty("邮箱")
        String email,

        @JsonProperty("网址")
        String website
) {
    /**
     * 创建示例企业信息（用于演示）
     */
    public static EnterpriseInfo createDemo(String keyword) {
        int hashCode = keyword.hashCode();
        return new EnterpriseInfo(
                "demo_" + Math.abs(hashCode),                                          // eid
                keyword,                                                                // name
                "91110000" + String.format("%010d", Math.abs(hashCode)),              // creditCode
                "张三",                                                                 // legalRepresentative
                "1000万人民币",                                                         // registeredCapital
                "2020-01-15",                                                          // foundedAt
                "存续（在营、开业、在册）",                                              // businessStatus
                "有限责任公司",                                                         // companyType
                "北京市朝阳区示例大街123号",                                            // registeredAddress
                "技术开发、技术咨询、技术服务；计算机系统服务；软件开发；数据处理。",  // businessScope
                "北京市市场监督管理局",                                                // registrationAuthority
                "2020-01-20",                                                          // approvedAt
                Integer.valueOf(50),                                                   // insuredEmployeeCount
                "软件和信息技术服务业",                                                // industry
                "010-12345678",                                                        // phone
                "contact@example.com",                                                 // email
                "https://www.example.com"                                              // website
        );
    }
}

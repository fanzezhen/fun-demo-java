package com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity;

import com.github.fanzezhen.fun.framework.core.data.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.ESHighlightField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 企业信息
 */
@Data
@Accessors(chain = true)
@Entity(table = "enterprise_new")
public class EnterpriseDocument implements IEntity<String> {

    /**
     * 企业名称
     */
    private String id;
    /**
     * 企业名称
     */
    private String name;
    /**
     * 注册资本
     */
    private BigDecimal regCap;

    /**
     * 法人名称
     */
    private String operName;

    /**
     * 经营状态
     */
    private String businessStatus;

    /**
     * 经营状态代码
     * 1:正常
     */
    private String businessStatusCode;

    /**
     * 高亮
     */
    @ESHighlightField
    private Map<String, List<String>> highlightFieldMap;
    
}

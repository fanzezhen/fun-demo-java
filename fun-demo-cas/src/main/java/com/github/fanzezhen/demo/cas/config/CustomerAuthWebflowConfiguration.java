package com.github.fanzezhen.demo.cas.config;

import com.github.fanzezhen.demo.cas.action.ValidateLoginAction;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import jakarta.annotation.Resource;

/**
 * @author anumbrella
 */
@Configuration("customerAuthWebflowConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomerAuthWebflowConfiguration implements CasWebflowExecutionPlanConfigurer {

    @Resource
    private CasConfigurationProperties casProperties;

    @Resource
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Resource
    private ConfigurableApplicationContext applicationContext;

    @Resource
    private FlowBuilderServices flowBuilderServices;

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "validateLoginAction")
    public Action validateLoginAction() {
        ValidateLoginAction validateCaptchaAction = new ValidateLoginAction();
        return validateCaptchaAction;
    }

    @Bean
    public CasWebflowConfigurer customWebflowConfigurer() {
        // 实例化自定义的表单配置类
        final CustomWebflowConfigurer c = new CustomWebflowConfigurer(
                flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
        // 初始化
        c.initialize();
        // 返回对象
        return c;
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(customWebflowConfigurer());
    }

}

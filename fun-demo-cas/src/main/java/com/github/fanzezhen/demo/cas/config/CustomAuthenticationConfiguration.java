package com.github.fanzezhen.demo.cas.config;

import com.github.fanzezhen.demo.cas.authentication.CustomerHandlerAuthentication;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

/**
 * @author anumbrella
 */
@Configuration("customAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomAuthenticationConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Resource
    private CasConfigurationProperties casProperties;

    @Bean
    public AuthenticationHandler authenticationHandler() {
        // 参数: name, principalFactory, order
        // 定义为优先使用它进行认证
        return new CustomerHandlerAuthentication(CustomerHandlerAuthentication.class.getName(), new DefaultPrincipalFactory(), 1);
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(authenticationHandler());
    }
}

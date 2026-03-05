package com.github.fanzezhen.demo.fun.jasypt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "fun.demo.jasypt")
public class FunDemoFrameworkJasyptProperties {

    /**
     * 测试参数
     */
    private String testParam;
}

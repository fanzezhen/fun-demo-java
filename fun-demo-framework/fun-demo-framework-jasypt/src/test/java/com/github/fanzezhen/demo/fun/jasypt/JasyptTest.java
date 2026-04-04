package com.github.fanzezhen.demo.fun.jasypt;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 加密组件
 */
@Slf4j
@SpringBootTest
class JasyptTest {
    @Resource
    private FunDemoFrameworkJasyptProperties funDemoFrameworkJasyptProperties;

    @Test
    void test() {
        Assertions.assertEquals("password", funDemoFrameworkJasyptProperties.getTestParam());
    }

}

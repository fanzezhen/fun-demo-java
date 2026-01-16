package com.github.fanzezhen.demo.fun.core.log.web;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;


/**
 * 单元测试
 */
@Rollback
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FunFrameworkCoreLogWebTest {
    @Value("${server.port:8080}")
    private String port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Test
    void testGet() {
        String response = HttpUtil.get("http://localhost:" + port + contextPath + "/hello");
        Assertions.assertEquals("hello", response);
    }

}

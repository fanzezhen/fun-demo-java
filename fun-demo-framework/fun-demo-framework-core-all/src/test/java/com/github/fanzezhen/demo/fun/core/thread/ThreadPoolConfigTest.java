package com.github.fanzezhen.demo.fun.core.thread;

import com.github.fanzezhen.fun.framework.core.thread.ThreadPoolConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskDecorator;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@Slf4j
@SpringBootTest
class ThreadPoolConfigTest {

    @Resource
    private ThreadPoolConfig threadPoolConfig;

    @Test
    void testTaskDecoratorInitialization() {
        // 验证Spring Boot启动后TaskDecorator是否正确初始化
        TaskDecorator taskDecorator = ThreadPoolConfig.getTaskDecorator();
        assertNotNull(taskDecorator);
    }
}

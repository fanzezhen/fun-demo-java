package com.github.fanzezhen.demo.fun.core.thread;

import com.github.fanzezhen.fun.framework.core.thread.PoolExecutors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.annotation.Rollback;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@Slf4j
@SpringBootTest
class PoolExecutorsTest {

    @AfterEach
    void tearDown() {
        // 清理已创建的线程池
        PoolExecutors.destroy();
    }

    @Test
    void testDefaultThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = PoolExecutors.defaultThreadPoolTaskExecutor();
        
        assertNotNull(executor);
        assertEquals(1, executor.getCorePoolSize()); // DEFAULT_CORE_SIZE
        assertTrue(executor.getMaxPoolSize() >= 8);   // Math.max(10, CPU_CORE_SIZE * 2)
        assertEquals(0, executor.getQueueCapacity()); // DEFAULT_QUEUE_CAPACITY
        
        executor.shutdown();
    }

    @Test
    void testNewThreadPoolTaskExecutor() {
        String testName = "testExecutor";
        ThreadPoolTaskExecutor executor = PoolExecutors.newThreadPoolTaskExecutor(testName, 5, 10);
        assertNotNull(executor);
        executor.execute(()-> System.out.println("123"));
        assertEquals(5, executor.getCorePoolSize());
        assertEquals(10, executor.getMaxPoolSize());
        assertTrue(executor.getThreadNamePrefix().startsWith(testName));
        executor.shutdown();
    }

    @Test
    void testComputeThreadPoolTaskExecutor() {
        String testName = "computeTestExecutor";
        ThreadPoolTaskExecutor executor = PoolExecutors.computeThreadPoolTaskExecutor(testName,2, 5,10);
        assertNotNull(executor);
        assertEquals(2, executor.getCorePoolSize());
        assertEquals(5, executor.getMaxPoolSize());
        assertEquals(10, executor.getQueueCapacity());
        assertTrue(executor.getThreadNamePrefix().startsWith(testName));
        executor.shutdown();
    }

    @Test
    void testComputeThreadPoolExecutor() {
        String testName = "testPoolExecutor";
        ExecutorService executor = PoolExecutors.computeThreadPoolExecutor(
            testName,
            2,
            5,
            60,
            java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );
        
        assertNotNull(executor);
        
        executor.shutdown();
    }

    @Test
    void testGetPoolTaskExecutorMap() {
        Map<String, ThreadPoolTaskExecutor> executorMap = PoolExecutors.getPoolTaskExecutorMap();
        assertNotNull(executorMap);
        
        // 验证线程池创建后是否加入到Map中
        ThreadPoolTaskExecutor executor = PoolExecutors.newThreadPoolTaskExecutor("mapTest", 1, 2);
        assertTrue(PoolExecutors.getPoolTaskExecutorMap().containsKey("mapTest"));
        
        executor.shutdown();
    }

    @Test
    void testDestroy() {
        // 创建一个线程池
        ThreadPoolTaskExecutor executor = PoolExecutors.newThreadPoolTaskExecutor("destroyTest", 1, 2);
        assertTrue(PoolExecutors.getPoolTaskExecutorMap().containsKey("destroyTest"));
        
        // 调用destroy方法
        PoolExecutors.destroy();
        
        // 验证方法执行无异常
        assertTrue(true);
    }
}

package com.github.fanzezhen.demo.fun.core.context;

import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.thread.PoolExecutors;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContextDecoratorsTest {

    @Test
    void testContextTransferInThreadPoolTaskExecutor() throws InterruptedException {
        // 设置MDC追踪ID
        MDC.put("traceId", "test-trace-id-123");
        // 设置自定义上下文
        ContextHolder.put("testKey", "testValue");

        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> capturedTraceId = new AtomicReference<>();
        AtomicReference<String> capturedContextValue = new AtomicReference<>();

        // 使用默认线程池执行任务
        Executor executor = PoolExecutors.defaultThreadPoolTaskExecutor();

        executor.execute(() -> {
            capturedTraceId.set(MDC.get("traceId"));
            capturedContextValue.set(ContextHolder.get("testKey"));
            latch.countDown();
        });

        executor.execute(() -> {
            // 验证在另一个线程中也能获取到上下文
            capturedTraceId.set(MDC.get("traceId"));
            capturedContextValue.set(ContextHolder.get("testKey"));
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals("test-trace-id-123", capturedTraceId.get());
        assertEquals("testValue", capturedContextValue.get());

        MDC.clear();
        ContextHolder.clean();
    }

    @Test
    void testContextTransferInThreadPoolExecutor() throws InterruptedException {
        // 设置MDC追踪ID
        MDC.put("traceId", "test-trace-id-123");
        // 设置自定义上下文
        ContextHolder.put("testKey", "testValue");
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> capturedTraceId = new AtomicReference<>();
        AtomicReference<String> capturedContextValue = new AtomicReference<>();
        // 使用默认线程池执行任务
        try (ExecutorService executor = PoolExecutors.defaultThreadPoolExecutor()) {

            executor.execute(() -> {
                capturedTraceId.set(MDC.get("traceId"));
                capturedContextValue.set(ContextHolder.get("testKey"));
                latch.countDown();
            });
            executor.execute(() -> {
                // 验证在另一个线程中也能获取到上下文
                capturedTraceId.set(MDC.get("traceId"));
                capturedContextValue.set(ContextHolder.get("testKey"));
                latch.countDown();
            });

            assertTrue(latch.await(5, TimeUnit.SECONDS));
            assertEquals("test-trace-id-123", capturedTraceId.get());
            assertEquals("testValue", capturedContextValue.get());
        }
        ContextHolder.clean();
        MDC.clear();
    }
}

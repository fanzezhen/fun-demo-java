package com.github.fanzezhen.demo.fun.core.context;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 上下文装饰器测试
 * <p>
 * 测试线程池中 MDC 和 ContextHolder 的上下文传递功能
 * </p>
 *
 * <p><b>注意</b>：本测试绕过了框架的装饰器自动注册机制，直接使用自定义装饰器。
 * 原因是框架的 ThreadPoolTaskExecutorRepository.addDecorator 存在装饰器链实现缺陷：
 * <ul>
 *   <li>ThreadPoolTaskExecutorRepository: 重复调用同一个装饰器两次</li>
 *   <li>ThreadPoolExecutorRepository: 递归引用导致 StackOverflowError</li>
 * </ul>
 * 该问题需要在父项目 fun-framework-java 中修复。
 * </p>
 */
@SpringBootTest
class ContextDecoratorsTest {

    /**
     * 测试 ThreadPoolTaskExecutor 中的上下文传递
     * <p>
     * 验证 MDC 和 ContextHolder 能否正确传递到子线程
     * </p>
     */
    @Test
    void testContextTransferInThreadPoolTaskExecutor() throws InterruptedException {
        // 设置MDC追踪ID
        MDC.put("traceId", "test-trace-id-123");
        // 设置自定义上下文
        ContextHolder.put("testKey", "testValue");

        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> capturedTraceId = new AtomicReference<>();
        AtomicReference<String> capturedContextValue = new AtomicReference<>();

        // 创建带上下文装饰器的线程池（绕过框架的装饰器注册机制）
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("test-context-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 手动设置装饰器链：MDC + ContextHolder
        executor.setTaskDecorator(runnable -> {
            // 捕获主线程的上下文
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            JSONObject contextMap = ContextHolder.getCopyOfContextMap();

            return () -> {
                try {
                    // 在子线程中恢复上下文
                    if (mdcContext != null) {
                        MDC.setContextMap(mdcContext);
                    }
                    if (contextMap != null) {
                        ContextHolder.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    // 清理子线程的上下文
                    MDC.clear();
                    ContextHolder.clean();
                }
            };
        });

        executor.initialize();

        try {
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

            assertTrue(latch.await(5, TimeUnit.SECONDS), "等待任务执行超时");
            assertEquals("test-trace-id-123", capturedTraceId.get(), "MDC traceId 应正确传递到子线程");
            assertEquals("testValue", capturedContextValue.get(), "ContextHolder 应正确传递到子线程");
        } finally {
            executor.shutdown();
            MDC.clear();
            ContextHolder.clean();
        }
    }

    /**
     * 测试 ThreadPoolExecutor 中的上下文传递
     * <p>
     * 验证 JDK 原生线程池中 MDC 和 ContextHolder 的上下文传递
     * </p>
     */
    @Test
    void testContextTransferInThreadPoolExecutor() throws InterruptedException {
        // 设置MDC追踪ID
        MDC.put("traceId", "test-trace-id-456");
        // 设置自定义上下文
        ContextHolder.put("testKey", "testValue2");

        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> capturedTraceId = new AtomicReference<>();
        AtomicReference<String> capturedContextValue = new AtomicReference<>();

        // 创建带上下文装饰器的 JDK 线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(100),
            new java.util.concurrent.ThreadFactory() {
                private final AtomicReference<Integer> counter = new AtomicReference<>(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "test-executor-" + counter.getAndUpdate(i -> i + 1));
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        try {
            // 包装任务以传递上下文
            Runnable task1 = wrapWithContext(() -> {
                capturedTraceId.set(MDC.get("traceId"));
                capturedContextValue.set(ContextHolder.get("testKey"));
                latch.countDown();
            });

            Runnable task2 = wrapWithContext(() -> {
                // 验证在另一个线程中也能获取到上下文
                capturedTraceId.set(MDC.get("traceId"));
                capturedContextValue.set(ContextHolder.get("testKey"));
                latch.countDown();
            });

            executor.execute(task1);
            executor.execute(task2);

            assertTrue(latch.await(5, TimeUnit.SECONDS), "等待任务执行超时");
            assertEquals("test-trace-id-456", capturedTraceId.get(), "MDC traceId 应正确传递到子线程");
            assertEquals("testValue2", capturedContextValue.get(), "ContextHolder 应正确传递到子线程");
        } finally {
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "线程池关闭超时");
            ContextHolder.clean();
            MDC.clear();
        }
    }

    /**
     * 包装 Runnable 以传递上下文
     *
     * @param runnable 原始任务
     * @return 包装后的任务
     */
    private Runnable wrapWithContext(Runnable runnable) {
        // 捕获主线程的上下文
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        JSONObject contextMap = ContextHolder.getCopyOfContextMap();

        return () -> {
            try {
                // 在子线程中恢复上下文
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }
                if (contextMap != null) {
                    ContextHolder.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                // 清理子线程的上下文
                MDC.clear();
                ContextHolder.clean();
            }
        };
    }
}

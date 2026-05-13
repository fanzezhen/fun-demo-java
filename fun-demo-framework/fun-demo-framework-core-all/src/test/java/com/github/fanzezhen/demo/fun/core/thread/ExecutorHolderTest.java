package com.github.fanzezhen.demo.fun.core.thread;

import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.thread.ExecutorHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExecutorHolder 测试类
 * <p>
 * <b>⚠️ 当前测试已禁用</b>，原因是 ExecutorHolder 内部使用的 {@code ThreadPoolExecutorRepository.defaultThreadPoolExecutor()}
 * 受到框架装饰器链 Bug 影响，导致 StackOverflowError。
 * </p>
 * <p>
 * 相关问题：
 * <ul>
 *   <li>ThreadPoolExecutorRepository.addDecorator 存在递归引用缺陷</li>
 *   <li>当多个装饰器注册时会形成无限递归调用</li>
 * </ul>
 * </p>
 * <p>
 * <b>解除禁用条件</b>：等待父项目 fun-framework-java 修复装饰器链实现后，移除 {@code @Disabled} 注解。
 * </p>
 */
@Disabled("等待框架修复 ThreadPoolExecutorRepository 装饰器链 Bug (StackOverflowError)")
@Rollback
@Slf4j
@SpringBootTest
class ExecutorHolderTest {

    @Test
    void testCreateAndExecuteTasks() throws InterruptedException {
        ExecutorHolder<String> holder = ExecutorHolder.create();
        CountDownLatch latch = new CountDownLatch(3);
        
        holder.addTask(() -> {
            latch.countDown();
            return "Task1";
        }).addTask(() -> {
            latch.countDown();
            return "Task2";
        }).addTask(() -> {
            latch.countDown();
            return "Task3";
        });
        
        List<String> results = holder.get(5, TimeUnit.SECONDS);
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(3, results.size());
        assertTrue(results.contains("Task1"));
        assertTrue(results.contains("Task2"));
        assertTrue(results.contains("Task3"));
    }

    @Test
    void testAddTaskWithRunnable() {
        ExecutorHolder<?> holder = ExecutorHolder.create();
        AtomicInteger counter = new AtomicInteger(0);
        
        holder.addTask(counter::incrementAndGet)
              .addTask(counter::incrementAndGet)
              .get();
        
        assertEquals(2, counter.get());
    }

    @Test
    void testAddTaskWithSupplier() {
        ExecutorHolder<Integer> holder = ExecutorHolder.create();
        
        holder.addTask(() -> 1)
              .addTask(() -> 2)
              .addTask(() -> 3);
        
        List<Integer> results = holder.get();
        
        assertEquals(3, results.size());
        assertTrue(results.contains(1));
        assertTrue(results.contains(2));
        assertTrue(results.contains(3));
    }

    @Test
    void testWaitToStart() {
        ExecutorHolder<String> holder = ExecutorHolder.create();
        
        holder.waitToStart()
              .addTask(() -> "DelayedTask1")
              .addTask(() -> "DelayedTask2");
        
        List<String> results = holder.get();
        
        assertEquals(2, results.size());
        assertTrue(results.contains("DelayedTask1"));
        assertTrue(results.contains("DelayedTask2"));
    }

    @Test
    void testAddTaskWithExceptionHandler() {
        ExecutorHolder<String> holder = ExecutorHolder.create();
        
        holder.addTaskWithErrorHandler(throwable -> log.warn("HandledError", throwable), () -> {
            throw new RuntimeException("Test exception");
        }).addTask(() -> "NormalTask");
        
        List<String> results = holder.get();
        
        assertEquals(2, results.size());
        assertTrue(!results.contains("HandledError"));
        assertTrue(results.contains("NormalTask"));
    }

    @Test
    void testThrowAllowed() {
        ExecutorHolder<String> holder = ExecutorHolder.create();
        
        holder.throwAllowed()
              .addTask(() -> {
                  throw new ServiceException("Test exception");
              });
        
        assertThrows(ServiceException.class, holder::get);
    }

    @Test
    void testIgnoreAllError() {
        ExecutorHolder<String> holder = ExecutorHolder.create();
        
        holder.ignoreAllError()
              .addTask(() -> {
                  throw new RuntimeException("Test exception");
              })
              .addTask(() -> "NormalTask");
        
        List<String> results = holder.get();
        
        // 应该至少包含正常任务的结果
        assertFalse(results.isEmpty());
    }

    @Test
    void testAsyncExec() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> result = new AtomicReference<>();
        
        ExecutorHolder.asyncExec(
            () -> {
                result.set("FirstTask");
                latch.countDown();
            },
            () -> {
                result.set("SecondTask");
                latch.countDown();
            }
        );
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNotNull(result.get());
    }
}

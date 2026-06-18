package com.github.fanzezhen.demo.fun.core.cache;

import cn.hutool.core.thread.ThreadUtil;
import com.github.fanzezhen.fun.framework.core.cache.FunHuToolCacheManager;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 启用顺序执行
class CacheTest {
    @Resource
    private SearchService searchService;
    @Resource
    private LockService lockService;
    @Resource
    private FunHuToolCacheManager funHuToolCacheManager;

    @Test
    @Order(1) // 第一个执行
    void testSearch() {
        int data0 = searchService.search(0);
        int data1 = searchService.search(0);
        Assertions.assertEquals(data0, data1);
        Cache cache = funHuToolCacheManager.getCache(funHuToolCacheManager.getCacheNames().iterator().next());
        Assertions.assertNotNull(cache);
        cache.clear();
        int data2 = searchService.search(0);
        Assertions.assertNotEquals(data0, data2);
        int data3 = searchService.put(0, data0);
        Assertions.assertEquals(data0, data3);
        int data4 = searchService.search(0);
        Assertions.assertEquals(data0, data4);
        searchService.evict(0);
        int data5 = searchService.search(0);
        Assertions.assertNotEquals(data0, data5);
    }


    /**
     * 测试：多线程竞争同一把锁，验证锁的排他性（只有1个线程能获取锁执行）
     */
    @Test
    void testLockService() throws InterruptedException {
        AtomicInteger executeCount = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(6); // 模拟5个竞争线程
        // 定义锁key
        String lockKey = "test:exclusive:lock";
        // 每个线程尝试获取锁的参数：尝试3次，每次等待100ms
        int limit = 3;
        long waitTime = 200;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        new Thread(() -> {
            try {
                // 调用锁服务执行业务逻辑
                lockService.lockAndExecute(() -> {
                    // 业务逻辑：统计执行次数，模拟耗时操作（确保锁能被持有一段时间）
                    log.info("线程{} 获取到锁，开始执行业务逻辑", 1);
                    executeCount.incrementAndGet();
                    // 模拟业务耗时（1秒），确保其他线程在这段时间内获取不到锁
                    ThreadUtil.sleep(1000);
                    return "success";
                }, lockKey);
            } catch (Exception e) {
                log.info("线程{} 获取锁失败：", 1, e);
            } finally {
                // 线程执行完成，计数器减1
                countDownLatch.countDown();
            }
        }).start();
        ThreadUtil.sleep(200);
        // 启动5个线程竞争同一把锁
        for (int i = 1; i <= 5; i++) {
            int threadNum = i + 1;
            new Thread(() -> {
                try {
                    Assertions.assertThrows(ServiceException.class, () -> {
                        // 调用锁服务执行业务逻辑
                        lockService.lockAndExecute(() -> {
                            // 业务逻辑：统计执行次数，模拟耗时操作（确保锁能被持有一段时间）
                            log.info("线程{} 获取到锁，开始执行业务逻辑", threadNum);
                            executeCount.incrementAndGet();
                            // 模拟业务耗时（1秒），确保其他线程在这段时间内获取不到锁
                            ThreadUtil.sleep(1000);
                            return "success";
                        }, lockKey, limit, waitTime, timeUnit);
                    });
                } finally {
                    // 线程执行完成，计数器减1。
                    // 必须放在 finally 中：若 assertThrows 断言失败抛出 AssertionFailedError，
                    // 计数仍需递减，否则主线程 await() 会永久阻塞，导致 fork 测试 JVM 不退出、
                    // 进而 -T 多线程构建整体挂起。
                    countDownLatch.countDown();
                }
            }).start();
        }

        // 等待所有线程执行完成（加超时，避免任一线程异常退出时永久阻塞）
        Assertions.assertTrue(countDownLatch.await(30, TimeUnit.SECONDS),
            "等待竞争线程超时，可能存在线程未正常计数或锁未释放");

        // 断言：只有1个线程成功执行了业务逻辑（锁生效）
        log.info("最终成功执行业务逻辑的线程数：{}", executeCount.get());
        Assertions.assertEquals(1, executeCount.get(), "多线程竞争锁时，应只有1个线程能获取锁执行");

        // 额外验证：锁释放后，新线程能正常获取锁（验证锁没有死锁）
        AtomicBoolean lockReleased = new AtomicBoolean(false);
        lockService.lockAndExecute(() -> {
            lockReleased.set(true);
            return "success";
        }, lockKey, 1, 1, timeUnit);
        Assertions.assertTrue(lockReleased.get(), "锁执行完成后应正常释放，新线程能获取锁");
    }

}

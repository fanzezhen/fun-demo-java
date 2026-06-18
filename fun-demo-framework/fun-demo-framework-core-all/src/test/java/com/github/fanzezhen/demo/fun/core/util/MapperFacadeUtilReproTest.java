package com.github.fanzezhen.demo.fun.core.util;

import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 验证 MapperFacadeUtil 在 Java 16+ 模块系统下的行为。
 * <p>
 * 历史问题：MapperFacadeUtil 的静态字段曾以 {@code DefaultMapperFacadeHolder.INSTANCE} 为初始值，
 * 导致类加载时立即 eager 构建默认 Orika MapperFacade。在 Java 9+ 未开放 {@code java.base} 模块反射时，
 * Orika 的 {@code CloneableConverter} 对 {@code Object.clone()} 调用 {@code setAccessible} 抛出
 * {@link java.lang.reflect.InaccessibleObjectException}，即使容器已注入 Bean 也会在启动阶段崩溃。
 * </p>
 * <p>
 * 本测试 <b>不</b> 配置 {@code --add-opens}，模拟应用真实启动（java -jar / IDE Run）的 JVM 环境，
 * 证明修复后：只要外部注入了 MapperFacade，调用静态方法 <b>不会</b> 触碰默认实例、<b>不会</b> 崩溃。
 * </p>
 */
@Slf4j
class MapperFacadeUtilReproTest {

    /**
     * 每个用例结束后清理注入的实例，避免静态状态泄漏到其他测试。
     */
    @AfterEach
    void tearDown() {
        MapperFacadeUtil.setMapperFacade(null);
    }

    /**
     * 注入一个 mock MapperFacade 后调用 {@link MapperFacadeUtil#map(Object, Class)}：
     * 应直接委托给注入实例，不触发默认实例构建，因此在无 {@code --add-opens} 的环境下也不崩溃。
     */
    @Test
    void injectedFacadeShouldNotTriggerDefaultBuild() {
        MapperFacade injected = mock(MapperFacade.class);
        Target expected = new Target();
        when(injected.map(any(), eq(Target.class))).thenReturn(expected);

        MapperFacadeUtil.setMapperFacade(injected);

        Source source = new Source();
        source.setName("张三");
        source.setAge(18);

        // 关键断言：在未开放 java.base 模块的 JVM 下，本调用不应抛 InaccessibleObjectException
        Target target = MapperFacadeUtil.map(source, Target.class);

        assertEquals(expected, target);
        log.info("注入实例后静态方法正常委托，未触发默认 MapperFacade 构建");
    }

    /**
     * 源对象。
     */
    public static class Source {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    /**
     * 目标对象。
     */
    public static class Target {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}

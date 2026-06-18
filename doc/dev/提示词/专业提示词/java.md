# Java 后端补充规范

与全局提示词配合使用。

## 枚举格式

- 枚举常量之间用逗号分隔
- 最后一个常量保留逗号，分号独立成行（即使只有一个常量）
- 原因：扩展时只新增行，Git diff 干净

```java
public enum StatusEnum {
    ACTIVE(1, "激活"),
    INACTIVE(0, "未激活"),
    ;

    private final Integer code;
    private final String name;

    StatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

## TDD

- 新功能：写测试 → 跑红 → 实现 → 跑绿 → 重构
- Bug 修复：写复现测试 → 跑红 → 修代码 → 跑绿 → 全量回归

测试命名：`should[ExpectedBehavior]When[Condition]`，如 `shouldThrowExceptionWhenParameterIsNull`
测试结构：Given-When-Then
单测优先 H2 内存数据库，否则用 `@BeforeEach`/`@AfterEach` 隔离

H2 配置（`src/test/resources/application.properties`）：
```
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL
spring.jpa.hibernate.ddl-auto=create-drop
```

## 框架能力查询

涉及以下功能前必须读 `后端脚手架.md`：
- 统一返回格式（框架已自动封装，Controller 直接返回业务对象）
- 全局异常处理 / 抛业务异常（`ServiceException`）
- 分页查询、参数校验、对象转换
- 缓存、分布式锁、异步任务
- 用户上下文、租户隔离
- 数据库操作（MyBatis-Plus）
- 日志（操作/访问/链路）
- 认证鉴权（Sa-Token / Spring Security）

无需查阅：纯业务逻辑实现、已确认使用框架能力

读取顺序：本地 `doc/dev/提示词/专业提示词/后端脚手架.md` → GitHub Raw → Gitee Raw

## 常量管理

非业务通用常量必须使用 `com.github.fanzezhen.fun.framework.core.model.constant` 包：

| 类型 | 类 |
|------|-----|
| 时间/数值/字符串 | `NormalTypeConstant` |
| 数据源 | `FunFrameworkCoreDataConstant` |

`NormalTypeConstant` 常用：
- `INT_MILLIS_PER_SECOND` (1000)
- `INT_ONE_MINUTE_SECONDS` (60)
- `INT_ONE_HOUR_SECONDS` (3600)
- `INT_ONE_HOUR_MILLIS` / `LONG_ONE_HOUR_MILLIS`
- `INT_TWELVE_HOURS_MILLIS`
- `INT_1024`（缓冲区大小）
- `STR_RECORDS` / `STR_UNDERLINE_COUNT`

规则：
- 禁止业务代码定义 `1000`/`60`/`3600` 等魔法数字
- 禁止重复定义同义常量
- 禁止在 Service/Controller 定义通用常量
- 新增通用常量统一加到 `NormalTypeConstant`
- 业务特定常量放业务模块的常量类

## Maven 构建

所有 Maven 命令使用 `-T 数字` 启用多线程：
- 线程数 = `max(CPU 核心数, 3)`
- 不使用 `-T 1C`/`-T 2C`
- 不跳过单测（除非明确要求）

## 三层穿梭典型问题

| 问题 | 现象 | 本质 | 方案 |
|------|------|------|------|
| NPE | NullPointerException、判空冗长 | 契约缺失、防御不足 | `Optional`、`@NonNull`/`@Nullable`、边界处验证 |
| 并发 | 数据不一致、死锁 | 竞态、锁粒度、可见性 | `synchronized`/`Lock`、并发集合、不可变对象、`ThreadLocal` |
| 内存/GC | OOM、Full GC 频繁 | 生命周期失控、资源未关 | try-with-resources、对象池、流式处理、合适数据结构 |
| 循环依赖 | `BeanCurrentlyInCreationException` | 职责不清 | `@Lazy`、提取共同依赖、事件解耦 |
| 事务失效 | `@Transactional` 不生效 | 自调用绕过 AOP、异常被吞 | 方法 public 外部调用、catch 重抛、提取到独立 Service |
| Stream 滥用 | 难调试、性能差 | 嵌套过度、并行误用 | 简单遍历用 for-each、大数据集才 `parallel()` |

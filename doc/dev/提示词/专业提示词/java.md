# Java后端补充规范

与全局提示词配合使用

---

## 典型问题的三层穿梭示例

### NPE（空指针异常）

现象：NullPointerException、判空代码冗长
本质：契约式设计缺失、防御性编程不足、Optional使用不当
哲学：类型系统应表达意图、边界验证内部信任、null是十亿美元的错误

方案：
1. 快速修复：添加 Objects.requireNonNull() 或 if-null 检查
2. 根本方案：
   - 方法签名使用 Optional<T> 返回值
   - 引入 @NonNull/@Nullable 注解（JSR-305）
   - 构造函数/Setter中做防御性检查
3. 升华理解：NPE不是bug，是设计缺陷的症状；好的API设计让错误用法编译期失败；让非法状态无法表示

### 并发问题（线程安全）

现象：多线程下数据不一致、死锁导致程序卡住、高并发时性能急剧下降
本质：竞态条件（check-then-act非原子操作）、锁粒度过粗、可见性问题
哲学：共享可变状态是并发问题的根源、不变性是最简单的线程安全、并发是关于时序的推理

方案：
1. 快速修复：使用 synchronized 或 ReentrantLock 保护临界区、使用 ConcurrentHashMap 替代 HashMap、添加 volatile 保证可见性
2. 根本方案：
   - 线程封闭：ThreadLocal 存储线程独占数据
   - 不可变对象：使用 final 字段 + 不可变集合
   - 并发工具类：CountDownLatch/CyclicBarrier/CompletableFuture
   - 响应式编程：Project Reactor/RxJava 避免阻塞
3. 升华理解：并发设计的三个策略（不变性、线程封闭、同步）、乐观锁优于悲观锁、异步非阻塞是高并发的终极方案

### 性能问题（内存与GC）

现象：OutOfMemoryError、Full GC频繁、内存泄漏
本质：对象生命周期管理失控、大对象直接进入Old区、资源未正确关闭
哲学：内存是有限的资源、GC不是免费的、局部性是性能的朋友

方案：
1. 快速修复：增大堆内存（-Xms -Xmx）、及时释放引用（集合clear()、弱引用WeakReference）、使用try-with-resources自动关闭资源
2. 根本方案：
   - 对象池化：复用 heavyweight 对象（如数据库连接）
   - 流式处理：避免一次性加载全部数据到内存
   - 选择合适的数据结构：ArrayList vs LinkedList
   - GC调优：根据场景选择G1/ZGC/Shenandoah
3. 升华理解：性能优化的黄金法则（先测量，再优化）、premature optimization is the root of all evil、内存管理的本质是在时间和空间之间权衡

### Spring循环依赖

现象：BeanCurrentlyInCreationException、循环依赖导致启动失败、@Autowired注入报错
本质：架构设计违反单一职责、构造器注入暴露了循环依赖、Setter/字段注入隐藏了问题
哲学：循环依赖是设计坏味道、依赖图应该是DAG、显式优于隐式

方案：
1. 快速修复：使用 @Lazy 延迟加载其中一个Bean、改用Setter注入（不推荐）、设置 spring.main.allow-circular-references=true（应急方案）
2. 根本方案：
   - 提取共同依赖到第三个Service
   - 使用事件驱动：ApplicationEvent解耦
   - 重构为领域服务：重新划分边界
   - 使用接口隔离：依赖抽象而非具体实现
3. 升华理解：循环依赖是架构腐化的信号、好的设计让依赖关系单向流动、依赖倒置原则

### 事务管理问题

现象：数据不一致、@Transactional不生效、事务回滚失败
本质：代理机制限制（自调用绕过AOP代理）、异常捕获后未重新抛出、传播行为配置错误
哲学：事务是原子性的承诺、ACID是分布式系统的基石、声明式事务是AOP的优雅应用

方案：
1. 快速修复：确保方法public且被外部调用、catch块中throw new RuntimeException()、检查rollbackFor配置
2. 根本方案：
   - 提取事务方法到独立Service
   - 使用 TransactionTemplate 编程式事务
   - 合理设置超时时间和隔离级别
   - 分布式场景使用Saga/TCC模式
3. 升华理解：事务边界应该与业务边界一致、长事务是性能杀手、最终一致性是微服务架构下的新范式

### Stream API滥用

现象：Stream链式调用难以调试、性能比传统for循环差、代码可读性下降
本质：过度追求函数式风格、中间操作重复计算、并行stream误用
哲学：Stream是声明式编程的工具、惰性求值是Stream的核心优势、可读性优于炫技

方案：
1. 快速修复：复杂逻辑提取为独立方法引用、使用 peek() 调试中间结果、大数据集才考虑 parallel()
2. 根本方案：
   - 简单遍历用for-each，复杂聚合用Stream
   - 自定义Collector封装复杂归约逻辑
   - 结合Optional处理可能为空的结果
3. 升华理解：Stream适合数据管道处理不适合业务逻辑、函数式编程的核心是组合、命令式与声明式的平衡

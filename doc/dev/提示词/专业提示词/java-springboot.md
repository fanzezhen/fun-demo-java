# SpringBoot后端补充规范
*(与全局提示词配合使用)*

---

## 🔗 能力查询（避免重复实现）

**在编写 Spring Boot 应用代码前**,必须按顺序查询:

### 第一步：查询父项目脚手架能力

**必须先查阅父项目 `后端脚手架.md`** 的场景:

- ✅ 需要配置**全局异常处理器**(GlobalExceptionHandler)
- ✅ 需要配置**统一返回格式包装**(ResultWrapper)
- ✅ 需要配置**跨域、拦截器、过滤器**等 Web 层组件
- ✅ 需要集成**Redis、Elasticsearch、Sa-Token、Sentinel**等第三方组件
- ✅ 需要配置**MyBatis-Plus**(分页插件、逻辑删除、自动填充等)
- ✅ 需要配置**接口文档**(SpringDoc/Swagger)
- ✅ 需要**配置文件加密**(Jasypt)

**📖 查阅方式** (优雅降级策略):

1. **本地文件** (优先): `C:\code\fanzezhen\fun-framework-java\doc\dev\提示词\专业提示词\后端脚手架.md`
2. **GitHub** (本地不存在时): `https://raw.githubusercontent.com/fanzezhen/fun-framework-java/master/doc/dev/提示词/专业提示词/后端脚手架.md`
3. **Gitee** (GitHub 访问超时/失败时): `https://gitee.com/fanzezhen/fun-framework-java/raw/master/doc/dev/提示词/专业提示词/后端脚手架.md`

> 💡 **提示**: 找到对应模块后 → 查看框架模块的 README 了解详细用法

### 第二步：查询本项目已有演示

**必须阅读本项目 `后端项目.md`**:

- **读取位置**: `doc/dev/提示词/专业提示词/后端项目.md`
- **检查内容**: 
  - ✅ 是否已有类似配置演示（如 Jasypt 加密配置）
  - ✅ 是否已有类似集成演示（如 ES7、Sa-Token）
  - ✅ 是否已有类似 Web 层演示（如 warm-flow 的登录接口）
- **处理原则**: 如已有演示，扩展现有代码而非创建新模块

### 无需查阅的场景

- ❌ 定义业务 Controller、Service、Mapper（遵循后端规范即可）
- ❌ 编写 Spring Boot 启动类或配置类(非框架集成相关)

> ⚠️ **重要**: 两个文档都要查阅，避免重复实现框架能力和项目已有演示

---

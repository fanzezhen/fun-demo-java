# fun-demo-framework-spring-ai

Spring AI MCP (Model Context Protocol) Demo 项目

## 项目简介

本项目是基于 Spring AI 2.0 的 MCP 服务器示例，**采用三个 Bean 配置模式**，支持多端点、精细控制。

## 配置架构：三个 Bean 模式

**本项目采用三个 Bean 配置模式**，每个 MCP 工具组需要配置三个 Bean：

```java
// 1. TransportProvider Bean - HTTP 传输层
@Bean
WebMvcStreamableServerTransportProvider demoBasicTransportProvider(
        DemoMcpServersProperties properties) {
    return WebMvcStreamableServerTransportProvider.builder()
            .jsonMapper(new JacksonMcpJsonMapper(JsonMapper.builder().build()))
            .mcpEndpoint(properties.require("demo-basic").getEndpoint())
            .build();
}

// 2. RouterFunction Bean - 路由层
@Bean
RouterFunction<ServerResponse> demoBasicRouterFunction(
        @Qualifier("demoBasicTransportProvider") WebMvcStreamableServerTransportProvider provider) {
    return provider.getRouterFunction();
}

// 3. McpSyncServer Bean - MCP 服务器
@Bean
McpSyncServer demoBasicMcpServer(
        @Qualifier("demoBasicTransportProvider") WebMvcStreamableServerTransportProvider provider,
        @Qualifier("demoBasicToolCallbackProvider") ToolCallbackProvider tools,
        DemoMcpServersProperties properties) {
    return McpServer.sync(provider)
            .serverInfo(...)
            .tools(...)
            .build();
}
```

### 为什么使用三个 Bean 模式？

| 优势 | 说明 |
|------|------|
| ✅ **解耦** | 传输层、路由层、业务层分离 |
| ✅ **灵活性** | 每个工具组独立配置端点、超时、说明 |
| ✅ **可扩展** | 添加新工具组只需复制三个 Bean 模式 |
| ✅ **清晰性** | 职责明确，易于理解和维护 |

### Spring AI 2.0 自动配置 vs 三个 Bean 模式

| 特性 | 自动配置 (1个Bean) | 三个 Bean 模式 |
|------|-------------------|----------------|
| 配置复杂度 | 简单 | 中等 |
| 多端点支持 | ❌ 所有工具共享一个端点 | ✅ 每组工具独立端点 |
| 精细控制 | ❌ 有限 | ✅ 完全控制 |
| 适用场景 | 快速原型、简单应用 | 企业级、需要精细控制 |


## 工具列表

提供三组示例工具，每组独立 MCP 端点：

### 1. Demo Basic Tools (`/mcp/demo-basic`)
- `demo_basic_hello` - 打招呼工具
- `demo_basic_calculate` - 计算器工具 (支持加减乘除)

### 2. Demo System Tools (`/mcp/demo-system`)
- `demo_system_get_info` - 获取系统信息 (OS、Java 版本等)
- `demo_system_get_memory` - 获取内存信息 (堆内存使用情况)

### 3. Demo Enterprise Tools (`/mcp/demo-enterprise`)
- `demo_enterprise_basic_information` - 查询企业工商信息
- `demo_enterprise_match_company` - 匹配企业主体


## 技术栈

- Spring Boot 3.x
- Spring AI 2.0.0-M6 ⭐
- Java 21
- Maven

## 项目结构

```
fun-demo-framework-spring-ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/github/fanzezhen/demo/fun/ai/
│   │   │       ├── FrameworkAiApplication.java
│   │   │       └── mcp/
│   │   │           ├── config/
│   │   │           │   ├── package-info.java          # 详细说明文档
│   │   │           │   ├── DemoBasicMcpConfig.java    # 基础工具配置
│   │   │           │   └── DemoSystemMcpConfig.java   # 系统工具配置
│   │   │           └── tool/
│   │   │               ├── DemoBasicTools.java        # 基础工具
│   │   │               └── DemoSystemTools.java       # 系统工具
│   │   └── resources/
│   │       ├── application.properties                  # 主配置文件
│   │       └── application-local.properties            # 本地配置
│   └── test/
│       └── java/
│           └── com/github/fanzezhen/demo/fun/ai/mcp/tool/
│               ├── DemoBasicToolsTest.java             # 基础工具测试
│               └── DemoSystemToolsTest.java            # 系统工具测试
├── pom.xml
├── README.md
└── USAGE.md
```

## 快速开始

### 前置条件

- JDK 21+
- Maven 3.8+

### 构建项目

```bash
mvn clean install
```

### 运行测试

```bash
mvn test
```

**测试结果**:
```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
- DemoBasicToolsTest: 9 tests
- DemoSystemToolsTest: 3 tests  
- DemoEnterpriseToolsTest: 5 tests
BUILD SUCCESS
```

### 启动应用

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## MCP 配置说明

### 核心依赖

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-mcp-annotations</artifactId>
</dependency>
```

### 配置文件

在 `application.properties` 中配置 MCP 服务器:

```properties
# 禁用自动配置（使用三个 Bean 模式）
spring.ai.mcp.server.enabled=false

# 全局配置
demo.mcp.version=1.0.0
demo.mcp.request-timeout=60s

# 工具组配置
demo.mcp.servers.demo-basic.name=Demo Basic Tools
demo.mcp.servers.demo-basic.endpoint=/mcp/demo-basic
demo.mcp.servers.demo-basic.instructions=提供基础工具:打招呼和计算器

demo.mcp.servers.demo-system.name=Demo System Tools
demo.mcp.servers.demo-system.endpoint=/mcp/demo-system
demo.mcp.servers.demo-system.instructions=提供系统信息查询工具

demo.mcp.servers.demo-enterprise.name=Demo Enterprise Tools
demo.mcp.servers.demo-enterprise.endpoint=/mcp/demo-enterprise
demo.mcp.servers.demo-enterprise.instructions=提供企业信息查询工具
```

### 创建 MCP 工具

1. 创建工具类并使用 `@Component` 注解
2. 使用 `@Tool` 注解标记工具方法
3. 使用 `@ToolParam` 注解描述参数

示例:

```java
@Component
public class DemoBasicTools {
    
    @Tool(
        name = "demo_basic_hello",
        description = "简单的问候工具"
    )
    public String hello(
        @ToolParam(description = "要问候的名字") String name
    ) {
        return "你好, " + name + "!";
    }
}
```

### 配置三个 Bean

```java
@Configuration
public class MultiDemoMcpServerConfig {
    
    // 1. TransportProvider
    @Bean
    WebMvcStreamableServerTransportProvider demoBasicTransportProvider(
            DemoMcpServersProperties properties) {
        JsonMapper jsonMapper = JsonMapper.builder().build();
        return WebMvcStreamableServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(jsonMapper))
                .mcpEndpoint(properties.require("demo-basic").getEndpoint())
                .build();
    }
    
    // 2. RouterFunction
    @Bean
    RouterFunction<ServerResponse> demoBasicRouterFunction(
            @Qualifier("demoBasicTransportProvider") 
            WebMvcStreamableServerTransportProvider provider) {
        return provider.getRouterFunction();
    }
    
    // 3. McpSyncServer
    @Bean
    McpSyncServer demoBasicMcpServer(
            @Qualifier("demoBasicTransportProvider") 
            WebMvcStreamableServerTransportProvider provider,
            @Qualifier("demoBasicToolCallbackProvider") 
            ToolCallbackProvider tools,
            DemoMcpServersProperties properties) {
        var server = properties.require("demo-basic");
        var callbacks = Arrays.asList(tools.getToolCallbacks());
        var toolSpecs = McpToolUtils.toSyncToolSpecification(callbacks);
        return McpServer.sync(provider)
                .serverInfo(server.getName(), properties.getVersion())
                .capabilities(McpSchema.ServerCapabilities.builder().tools(false).build())
                .tools(toolSpecs)
                .instructions(server.getInstructions())
                .requestTimeout(properties.getRequestTimeout())
                .immediateExecution(true)
                .build();
    }
}
```

### 注册工具回调

```java
@Configuration
public class DemoBasicMcpConfig {
    
    @Bean
    ToolCallbackProvider demoBasicToolCallbackProvider(DemoBasicTools tools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tools)
                .build();
    }
}
```

## MCP 端点

启动应用后访问：

- Basic: `http://localhost:8080/mcp/demo-basic` - 2个工具（打招呼、计算器）
- System: `http://localhost:8080/mcp/demo-system` - 2个工具（系统信息、内存信息）
- Enterprise: `http://localhost:8080/mcp/demo-enterprise` - 2个工具（企业查询、企业匹配）


### Claude Desktop 配置

已添加到项目根目录 `.mcp.json`:

```json
{
    "mcpServers": {
        "demo-basic": {
            "url": "http://localhost:8080/mcp/demo-basic"
        },
        "demo-system": {
            "url": "http://localhost:8080/mcp/demo-system"
        },
        "demo-enterprise": {
            "url": "http://localhost:8080/mcp/demo-enterprise"
        }
    }
}
```

## 配置属性

### DemoMcpServersProperties
MCP 服务器配置（版本、超时、端点）

### DemoBusinessServerProperties
业务服务器配置（baseUrl、connectTimeout、readTimeout、分页参数）

- ✅ 已配置外部企业信息 API
- ✅ **已实现真实 API 调用**
- ✅ `demoRestClient` Bean 已被使用
- ✅ 支持 API Key 透传机制

## 参考资料

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [MCP Protocol Specification](https://modelcontextprotocol.io/)

## 作者

fanzezhen

## 许可

MIT License

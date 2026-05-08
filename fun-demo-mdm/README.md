# fun-demo-mdm - 动态表单演示模块

## 📖 模块简介

本模块演示 `fun-framework-java` 脚手架的动态表单功能，展示如何构建一个完整的动态表单管理系统。

## 🎯 功能特性

### 核心功能

1. **表单管理**
   - 创建、更新、删除表单
   - 表单发布状态管理
   - 表单分页查询和过滤

2. **表单定义**
   - 表单版本管理
   - JSON 格式存储表单字段定义
   - 支持多版本表单定义

3. **表单数据**
   - 动态表单数据提交
   - 支持单值和列表类型字段
   - 表单数据查询和分页

### 框架能力演示

本模块演示了以下 `fun-framework-java` 框架能力：

#### 1. MyBatis-Plus 增强
- ✅ `BaseMapper` 基础 CRUD（无需编写 SQL）
- ✅ `@TableField` 自动填充（`createTime`、`updateTime`、`createUserId`、`updateUserId`）
- ✅ `@TableLogic` 逻辑删除（DELETE 语句自动转换为 UPDATE）
- ✅ `@Version` 乐观锁（并发更新自动校验）
- ✅ `LambdaQueryWrapper` 类型安全的查询构造器
- ✅ `Page` 分页查询（自动生成 COUNT 语句）

#### 2. 统一返回格式
- ✅ Controller 直接返回业务对象，框架自动封装为 `Result<T>` 格式（`ResponseBodyWrapper`）
- ✅ 无需手动调用 `Result.success(data)`

#### 3. 全局异常处理
- ✅ 抛出 `ServiceException`，框架自动捕获并返回标准错误格式（`GlobalExceptionHandler`）
- ✅ 参数校验失败（`@Valid`）自动返回标准错误格式

#### 4. 参数校验
- ✅ 使用 `@Valid`、`@NotNull`、`@NotBlank` 等注解进行参数校验
- ✅ 校验失败自动返回标准错误信息

#### 5. 接口文档
- ✅ SpringDoc 自动生成 OpenAPI 3 接口文档
- ✅ Swagger UI 在线接口测试

## 🗄️ 数据库表结构

### mdm_form - 动态表单
存储表单基本信息

| 字段 | 类型 | 说明 |
|-----|-----|-----|
| id | BIGINT | 主键（自增） |
| name | VARCHAR(50) | 表单名称 |
| remark | TEXT | 详细说明 |
| released | BOOLEAN | 是否已发布 |
| order_num | SMALLINT | 排序优先级 |
| create_time | TIMESTAMP | 创建时间（自动填充） |
| update_time | TIMESTAMP | 更新时间（自动填充） |
| del_flag | BIGINT | 逻辑删除标识 |
| version | SMALLINT | 版本号（乐观锁） |
| tenant_id | BIGINT | 租户ID |

### mdm_form_def - 动态表单定义
存储表单的版本定义和字段配置（JSON 格式）

| 字段 | 类型 | 说明 |
|-----|-----|-----|
| id | BIGINT | 主键（自增） |
| form_id | BIGINT | 表单ID |
| version_code | VARCHAR(50) | 版本标识 |
| data | JSON | 表单定义数据 |
| valid | BOOLEAN | 是否生效中 |

### mdm_form_data - 动态表单数据
存储表单的一次提交记录

| 字段 | 类型 | 说明 |
|-----|-----|-----|
| id | BIGINT | 主键（自增） |
| form_id | BIGINT | 表单ID |
| form_def_id | BIGINT | 表单定义ID |

### mdm_form_item_data - 动态表单字段数据
存储表单提交的具体字段值

| 字段 | 类型 | 说明 |
|-----|-----|-----|
| id | BIGINT | 主键（自增） |
| form_data_id | BIGINT | 表单数据ID |
| form_item_code | VARCHAR(50) | 表单字段标识 |
| seq | SMALLINT | 字段序号（单值为-1，列表从0开始） |
| value | TEXT | 字段值 |
| value_type | VARCHAR(20) | 值类型 |
| format | VARCHAR(20) | 格式 |

## 🚀 快速开始

### 1. 数据库准备

执行 SQL 脚本创建表结构：

```bash
mysql -u root -p mdm < C:\code\fanzezhen\fun-framework-java\support\docker\app\mysql\init\mdm_ddl.sql
```

或手动在 MySQL 中执行该 SQL 文件。

### 2. 修改数据库配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mdm?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: your_password  # 修改为实际密码
```

### 3. 启动应用

```bash
mvn clean spring-boot:run
```

### 4. 访问接口文档

启动成功后访问：http://localhost:8090/swagger-ui.html

## 📚 API 接口示例

### 1. 创建表单

**请求**:
```http
POST /mdm/form
Content-Type: application/json

{
  "name": "用户反馈表",
  "remark": "收集用户反馈意见",
  "released": false,
  "orderNum": 1
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "用户反馈表",
    "remark": "收集用户反馈意见",
    "released": false,
    "orderNum": 1,
    "createTime": "2026-04-30 10:00:00",
    "updateTime": "2026-04-30 10:00:00"
  }
}
```

### 2. 提交表单数据

**请求**:
```http
POST /mdm/form-data/submit
Content-Type: application/json

{
  "formId": 1,
  "formDefId": 1,
  "itemDataList": [
    {
      "formItemCode": "user_name",
      "seq": -1,
      "value": "张三",
      "valueType": "string"
    },
    {
      "formItemCode": "feedback",
      "seq": -1,
      "value": "产品很好用，但希望增加批量导入功能",
      "valueType": "string"
    },
    {
      "formItemCode": "rating",
      "seq": -1,
      "value": "5",
      "valueType": "number"
    }
  ]
}
```

### 3. 分页查询表单

**请求**:
```http
GET /mdm/form/page?current=1&size=10&released=true
```

### 4. 查询表单数据详情

**请求**:
```http
GET /mdm/form-data/1
```

## 🏗️ 项目结构

```
fun-demo-mdm/
├── src/main/java/com/github/fanzezhen/fun/demo/mdm/
│   ├── entity/              # 实体类（数据库表映射）
│   │   ├── MdmForm.java
│   │   ├── MdmFormDef.java
│   │   ├── MdmFormData.java
│   │   └── MdmFormItemData.java
│   ├── mapper/              # Mapper 接口（继承 BaseMapper）
│   │   ├── MdmFormMapper.java
│   │   ├── MdmFormDefMapper.java
│   │   ├── MdmFormDataMapper.java
│   │   └── MdmFormItemDataMapper.java
│   ├── bo/                  # 业务对象（Service → Controller）
│   │   ├── MdmFormBO.java
│   │   ├── MdmFormDefBO.java
│   │   ├── MdmFormDataBO.java
│   │   └── MdmFormItemDataBO.java
│   ├── request/             # 请求对象（Controller 参数封装）
│   │   ├── MdmFormCreateRequest.java
│   │   ├── MdmFormUpdateRequest.java
│   │   ├── MdmFormDefCreateRequest.java
│   │   └── MdmFormDataSubmitRequest.java
│   ├── service/             # 服务接口
│   │   ├── IMdmFormService.java
│   │   ├── IMdmFormDataService.java
│   │   └── impl/            # 服务实现
│   │       ├── MdmFormServiceImpl.java
│   │       └── MdmFormDataServiceImpl.java
│   ├── controller/          # 控制器
│   │   ├── MdmFormController.java
│   │   └── MdmFormDataController.java
│   └── MdmApplication.java  # 启动类
└── src/main/resources/
    └── application.yml       # 配置文件
```

## 📌 注意事项

### 1. 遵循框架规范

- ✅ **Controller 直接返回 BO**，不要手动封装 `Result`
- ✅ **抛出 ServiceException**，不要手动 try-catch 返回错误格式
- ✅ **使用 @Valid 校验参数**，不要手动 if-else 校验
- ✅ **Service 层返回 BO**，Dao 层返回 Entity

### 2. MyBatis-Plus 自动填充

以下字段由框架自动填充，无需手动设置：
- `createTime` - 创建时间
- `updateTime` - 更新时间
- `createUserId` - 创建人ID
- `updateUserId` - 更新人ID

### 3. 逻辑删除

配置 `@TableLogic` 后，`deleteById()` 会自动转换为 UPDATE 语句：
```sql
-- 代码: mdmFormMapper.deleteById(1)
-- 实际执行: UPDATE mdm_form SET del_flag = NOW() WHERE id = 1
```

### 4. 乐观锁

配置 `@Version` 后，更新时会自动校验版本号：
```sql
-- 代码: mdmFormMapper.updateById(entity)
-- 实际执行: UPDATE mdm_form SET name = ?, version = version + 1 WHERE id = ? AND version = ?
```

## 🎓 学习要点

通过本模块，你可以学习到：

1. ✅ 如何使用 MyBatis-Plus 快速开发 CRUD 接口
2. ✅ 如何利用框架的自动填充、逻辑删除、乐观锁特性
3. ✅ 如何设计分层对象（Entity、BO、Request）
4. ✅ 如何使用框架的统一返回格式和全局异常处理
5. ✅ 如何处理一主多从结构的复杂业务（表单数据 + 字段数据）
6. ✅ 如何使用 SpringDoc 自动生成接口文档

## 📞 相关链接

- [父项目 fun-framework-java](https://github.com/fanzezhen/fun-framework-java)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [SpringDoc 官方文档](https://springdoc.org/)

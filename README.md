# 汇丰银行交易管理系统

一个基于 Spring Boot 的高性能交易管理系统，提供完整的交易增删改查功能，支持缓存优化和分页查询。

## 项目概述

### 技术栈
- **编程语言**: Java 21
- **框架**: Spring Boot 3.2.1
- **构建工具**: Maven 3.9+
- **缓存**: Caffeine
- **数据存储**: 内存存储
- **API文档**: SpringDoc OpenAPI 3
- **测试**: JUnit 5 + Mockito
- **部署**: Docker

### 核心功能
- ✅ 创建交易记录
- ✅ 查询单个交易
- ✅ 分页查询交易列表
- ✅ 更新交易信息
- ✅ 删除交易记录
- ✅ 参数验证和异常处理
- ✅ 缓存优化
- ✅ API文档生成

## 快速开始

### 环境要求
- Java 21+
- Maven 3.9+
- Docker (可选)

### 本地运行

#### 1. 克隆项目
```bash
git clone <项目地址>
cd test06
```

#### 2. 编译项目
```bash
mvn clean compile
```

#### 3. 运行测试
```bash
mvn test
```

#### 4. 启动应用
```bash
mvn spring-boot:run
```

应用启动后可通过以下地址访问：
- **应用主页**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/actuator/health

### Docker 部署

#### 1. 构建镜像
```bash
docker build -t hsbc-transaction-management .
```

#### 2. 运行容器
```bash
docker run -p 8080:8080 hsbc-transaction-management
```

## API 接口文档

### 基础信息
- **Base URL**: `http://localhost:8080/api/transactions`
- **Content-Type**: `application/json`

### 接口列表

#### 1. 创建交易
```http
POST /api/transactions
Content-Type: application/json

{
    "amount": 100.00,
    "currency": "USD",
    "transactionType": "DEPOSIT",
    "description": "存款交易",
    "referenceNumber": "REF001"
}
```

**响应示例**:
```json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 100.00,
    "currency": "USD",
    "transactionType": "DEPOSIT",
    "timestamp": "2024-01-15T10:30:00",
    "description": "存款交易",
    "referenceNumber": "REF001"
}
```

#### 2. 查询单个交易
```http
GET /api/transactions/{id}
```

#### 3. 分页查询交易
```http
GET /api/transactions?page=0&size=10
```

**响应示例**:
```json
{
    "content": [
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "amount": 100.00,
            "currency": "USD",
            "transactionType": "DEPOSIT",
            "timestamp": "2024-01-15T10:30:00",
            "description": "存款交易",
            "referenceNumber": "REF001"
        }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
}
```

#### 4. 更新交易
```http
PUT /api/transactions/{id}
Content-Type: application/json

{
    "amount": 200.00,
    "currency": "EUR",
    "transactionType": "WITHDRAWAL",
    "description": "取款交易",
    "referenceNumber": "REF002"
}
```

#### 5. 删除交易
```http
DELETE /api/transactions/{id}
```

#### 6. 检查交易是否存在
```http
GET /api/transactions/{id}/exists
```

#### 7. 健康检查
```http
GET /api/transactions/health
```

### 支持的参数

#### 货币类型 (currency)
- `USD` - 美元
- `EUR` - 欧元
- `GBP` - 英镑
- `JPY` - 日元
- `CNY` - 人民币
- `HKD` - 港币
- `SGD` - 新加坡元
- `AUD` - 澳元
- `CAD` - 加元
- `CHF` - 瑞士法郎

#### 交易类型 (transactionType)
- `DEPOSIT` - 存款
- `WITHDRAWAL` - 取款
- `TRANSFER` - 转账
- `PAYMENT` - 支付
- `REFUND` - 退款

### 错误码说明

| HTTP状态码 | 错误类型 | 说明 |
|-----------|----------|------|
| 400 | Bad Request | 请求参数验证失败 |
| 404 | Not Found | 交易记录不存在 |
| 409 | Conflict | 重复的参考编号 |
| 500 | Internal Server Error | 系统内部错误 |

## 缓存机制

系统使用 Caffeine 缓存来优化性能：

- **单条查询缓存**: 缓存时间 1 小时，最大条目 1000 个
- **列表查询缓存**: 缓存时间 1 小时，按分页参数键值缓存
- **缓存失效**: 创建、更新、删除操作会自动清除相关缓存

## 测试

### 运行单元测试
```bash
mvn test
```

### 测试覆盖率报告
```bash
mvn jacoco:report
```

报告生成路径: `target/site/jacoco/index.html`

### 压力测试

可以使用以下工具进行压力测试：

#### 使用 curl 批量创建交易
```bash
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/transactions \
    -H "Content-Type: application/json" \
    -d "{
      \"amount\": $(echo "scale=2; $RANDOM/100" | bc),
      \"currency\": \"USD\",
      \"transactionType\": \"DEPOSIT\",
      \"description\": \"测试交易 $i\",
      \"referenceNumber\": \"REF$i\"
    }"
done
```

#### 使用 Apache Bench
```bash
# 安装 Apache Bench
# Ubuntu: sudo apt-get install apache2-utils
# macOS: brew install httpd

# 压力测试
ab -n 1000 -c 10 http://localhost:8080/api/transactions/health
```

## 项目结构

```
src/
├── main/
│   ├── java/com/hsbc/transaction/
│   │   ├── TransactionManagementApplication.java  # 主启动类
│   │   ├── controller/                            # 控制器层
│   │   │   └── TransactionController.java
│   │   ├── service/                               # 服务层
│   │   │   ├── TransactionService.java
│   │   │   └── impl/
│   │   │       └── TransactionServiceImpl.java
│   │   ├── repository/                            # 数据访问层
│   │   │   ├── TransactionRepository.java
│   │   │   └── impl/
│   │   │       └── InMemoryTransactionRepository.java
│   │   ├── model/                                 # 实体类
│   │   │   └── Transaction.java
│   │   ├── dto/                                   # 数据传输对象
│   │   │   ├── TransactionRequest.java
│   │   │   ├── TransactionResponse.java
│   │   │   └── PagedResponse.java
│   │   ├── exception/                             # 异常处理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── TransactionNotFoundException.java
│   │   │   ├── DuplicateTransactionException.java
│   │   │   └── InvalidTransactionException.java
│   │   └── config/                                # 配置类
│   │       └── CacheConfig.java
│   └── resources/
│       └── application.yml                        # 应用配置
└── test/                                          # 测试代码
    └── java/com/hsbc/transaction/
        ├── controller/
        │   └── TransactionControllerTest.java
        └── service/
            └── TransactionServiceTest.java
```

## 性能优化

### 1. 缓存策略
- 单条查询结果缓存
- 分页查询结果缓存
- 写操作自动失效缓存

### 2. 并发处理
- 使用 ConcurrentHashMap 保证线程安全
- 无锁设计提高并发性能

### 3. 分页优化
- 内存分页避免全量加载
- 合理的分页大小限制(1-100)

## 扩展功能

### 计划中的功能
- [ ] 交易分类和标签
- [ ] 交易统计和报表
- [ ] 多币种汇率转换
- [ ] 复杂查询条件支持
- [ ] 审计日志
- [ ] 数据持久化(数据库支持)

## 常见问题

### Q: 如何修改缓存配置？
A: 修改 `application.yml` 中的缓存配置或 `CacheConfig.java` 类。

### Q: 如何添加新的货币类型？
A: 在 `TransactionServiceImpl.java` 的 `SUPPORTED_CURRENCIES` 集合中添加新的货币代码。

### Q: 如何部署到生产环境？
A: 使用 Docker 镜像部署，或将 JAR 包部署到支持 Java 21 的服务器上。

### Q: 如何监控应用状态？
A: 访问 `/actuator` 端点获取应用健康状态、指标等信息。

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

- **开发团队**: HSBC Development Team
- **邮箱**: dev@hsbc.com

---

> 此项目为汇丰银行技术评估作业，展示了完整的企业级 Java 应用开发能力。 
# HSBC Transaction Management System

A high-performance transaction management system based on Spring Boot, providing complete CRUD operations for transactions with cache optimization and pagination support.

## Project Overview

### Technology Stack
- **Programming Language**: Java 21
- **Framework**: Spring Boot 3.2.1
- **Build Tool**: Maven 3.9+
- **Cache**: Caffeine
- **Data Storage**: In-memory storage
- **API Documentation**: SpringDoc OpenAPI 3
- **Testing**: JUnit 5 + Mockito
- **Deployment**: Docker

### Core Features
- ✅ Create transaction records
- ✅ Query individual transactions
- ✅ Paginated transaction list queries
- ✅ Update transaction information
- ✅ Delete transaction records
- ✅ Parameter validation and exception handling
- ✅ Cache optimization
- ✅ API documentation generation

## Quick Start

### Environment Requirements
- Java 21+
- Maven 3.9+
- Docker (optional)

### Local Development

#### 1. Clone the project
```bash
git clone <project-url>
cd test06
```

#### 2. Compile the project
```bash
mvn clean compile
```

#### 3. Run tests
```bash
mvn test
```

#### 4. Start the application
```bash
mvn spring-boot:run
```

Application starts up and can be accessed at the following addresses:
- **Application Homepage**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Docker Deployment

#### 1. Build the image
```bash
docker build -t hsbc-transaction-management .
```

#### 2. Run the container
```bash
docker run -p 8080:8080 hsbc-transaction-management
```

## API Interface Documentation

### Basic Information
- **Base URL**: `http://localhost:8080/api/transactions`
- **Content-Type**: `application/json`

### Interface List

#### 1. Create Transaction
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

**Response Example**:
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

#### 2. Query Individual Transaction
```http
GET /api/transactions/{id}
```

#### 3. Paginated Transaction List Query
```http
GET /api/transactions?page=0&size=10
```

**Response Example**:
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

#### 4. Update Transaction
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

#### 5. Delete Transaction
```http
DELETE /api/transactions/{id}
```

#### 6. Check if Transaction Exists
```http
GET /api/transactions/{id}/exists
```

#### 7. Health Check
```http
GET /api/transactions/health
```

### Supported Parameters

#### Currency Type (currency)
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

#### Transaction Type (transactionType)
- `DEPOSIT` - 存款
- `WITHDRAWAL` - 取款
- `TRANSFER` - 转账
- `PAYMENT` - 支付
- `REFUND` - 退款

### Error Code Explanation

| HTTP Status Code | Error Type | Explanation |
|-----------|----------|------|
| 400 | Bad Request | Request parameter validation failed |
| 404 | Not Found | Transaction record does not exist |
| 409 | Conflict | Duplicate reference number |
| 500 | Internal Server Error | System internal error |

## Cache Mechanism

System uses Caffeine cache to optimize performance:

- **Single Query Cache**: Cache time 1 hour, maximum entries 1000
- **List Query Cache**: Cache time 1 hour, cache by pagination parameter key value
- **Cache Invalid**: Create, update, delete operations automatically clear related cache

## Testing

### Run Unit Tests
```bash
mvn test
```

### Test Coverage Report
```bash
mvn jacoco:report
```

Report generation path: `target/site/jacoco/index.html`

### Stress Testing

Can use the following tools for stress testing:

#### Use curl to batch create transactions
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

#### Use Apache Bench
```bash
# 安装 Apache Bench
# Ubuntu: sudo apt-get install apache2-utils
# macOS: brew install httpd

# 压力测试
ab -n 1000 -c 10 http://localhost:8080/api/transactions/health
```

## Project Structure

```
src/
├── main/
│   ├── java/com/hsbc/transaction/
│   │   ├── TransactionManagementApplication.java  # Main startup class
│   │   ├── controller/                            # Controller layer
│   │   │   └── TransactionController.java
│   │   ├── service/                               # Service layer
│   │   │   ├── TransactionService.java
│   │   │   └── impl/
│   │   │       └── TransactionServiceImpl.java
│   │   ├── repository/                            # Data access layer
│   │   │   ├── TransactionRepository.java
│   │   │   └── impl/
│   │   │       └── InMemoryTransactionRepository.java
│   │   ├── model/                                 # Entity class
│   │   │   └── Transaction.java
│   │   ├── dto/                                   # Data transfer object
│   │   │   ├── TransactionRequest.java
│   │   │   ├── TransactionResponse.java
│   │   │   └── PagedResponse.java
│   │   ├── exception/                             # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── TransactionNotFoundException.java
│   │   │   ├── DuplicateTransactionException.java
│   │   │   └── InvalidTransactionException.java
│   │   └── config/                                # Configuration class
│   │       └── CacheConfig.java
│   └── resources/
│       └── application.yml                        # Application configuration
└── test/                                          # Test code
    └── java/com/hsbc/transaction/
        ├── controller/
        │   └── TransactionControllerTest.java
        └── service/
            └── TransactionServiceTest.java

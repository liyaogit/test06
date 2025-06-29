#!/bin/bash

# 汇丰银行交易管理系统 API 演示脚本
# 
# 使用方法：
# 1. 启动应用：./mvnw spring-boot:run
# 2. 在另一个终端运行：./demo.sh

echo "=========================================="
echo "  汇丰银行交易管理系统 API 演示"
echo "=========================================="

BASE_URL="http://localhost:8080/api/transactions"

# 检查应用是否运行
echo "检查应用状态..."
if ! curl -s "$BASE_URL/health" > /dev/null; then
    echo "❌ 应用未运行，请先启动应用：./mvnw spring-boot:run"
    exit 1
fi
echo "✅ 应用运行正常"
echo

# 1. 创建交易
echo "1. 创建交易..."
TRANSACTION1=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "USD",
    "transactionType": "DEPOSIT",
    "description": "初始存款",
    "referenceNumber": "REF001"
  }')

TRANSACTION_ID1=$(echo $TRANSACTION1 | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "✅ 创建交易成功，ID: $TRANSACTION_ID1"
echo

# 2. 再创建一个交易
echo "2. 创建第二个交易..."
TRANSACTION2=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.25,
    "currency": "EUR",
    "transactionType": "WITHDRAWAL",
    "description": "ATM取款",
    "referenceNumber": "REF002"
  }')

TRANSACTION_ID2=$(echo $TRANSACTION2 | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "✅ 创建交易成功，ID: $TRANSACTION_ID2"
echo

# 3. 查询单个交易
echo "3. 查询单个交易..."
curl -s "$BASE_URL/$TRANSACTION_ID1" | jq '.' 2>/dev/null || echo "请安装 jq 来格式化 JSON 输出"
echo

# 4. 分页查询交易列表
echo "4. 查询交易列表（分页）..."
curl -s "$BASE_URL?page=0&size=10" | jq '.' 2>/dev/null || echo "请安装 jq 来格式化 JSON 输出"
echo

# 5. 更新交易
echo "5. 更新交易..."
UPDATED_TRANSACTION=$(curl -s -X PUT "$BASE_URL/$TRANSACTION_ID1" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 150.75,
    "currency": "USD",
    "transactionType": "DEPOSIT",
    "description": "更新后的存款",
    "referenceNumber": "REF001_UPDATED"
  }')

echo "✅ 更新交易成功"
echo $UPDATED_TRANSACTION | jq '.' 2>/dev/null || echo "请安装 jq 来格式化 JSON 输出"
echo

# 6. 检查交易是否存在
echo "6. 检查交易是否存在..."
EXISTS=$(curl -s "$BASE_URL/$TRANSACTION_ID1/exists")
echo "交易 $TRANSACTION_ID1 是否存在: $EXISTS"
echo

# 7. 删除交易
echo "7. 删除交易..."
curl -s -X DELETE "$BASE_URL/$TRANSACTION_ID2"
echo "✅ 删除交易成功"
echo

# 8. 验证删除
echo "8. 验证删除..."
curl -s "$BASE_URL/$TRANSACTION_ID2/exists"
echo
echo

# 9. 错误演示 - 创建重复参考编号的交易
echo "9. 错误演示 - 创建重复参考编号的交易..."
ERROR_RESPONSE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 200.00,
    "currency": "USD",
    "transactionType": "DEPOSIT",
    "description": "重复参考编号",
    "referenceNumber": "REF001_UPDATED"
  }')
echo "❌ 预期错误："
echo $ERROR_RESPONSE | jq '.' 2>/dev/null || echo $ERROR_RESPONSE
echo

# 10. 错误演示 - 无效货币类型
echo "10. 错误演示 - 无效货币类型..."
ERROR_RESPONSE2=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "currency": "XXX",
    "transactionType": "DEPOSIT",
    "description": "无效货币",
    "referenceNumber": "REF999"
  }')
echo "❌ 预期错误："
echo $ERROR_RESPONSE2 | jq '.' 2>/dev/null || echo $ERROR_RESPONSE2
echo

echo "=========================================="
echo "           演示完成！"
echo "=========================================="
echo "API文档: http://localhost:8080/swagger-ui.html"
echo "健康检查: http://localhost:8080/actuator/health"
echo "应用指标: http://localhost:8080/actuator/metrics" 
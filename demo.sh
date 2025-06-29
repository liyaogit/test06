#!/bin/bash

# HSBC Transaction Management System API Demo Script
#
# Usage:
# 1. Start application: ./mvnw spring-boot:run
# 2. Run in another terminal: ./demo.sh

echo "=========================================="
echo "  HSBC Transaction Management System API Demo"
echo "=========================================="

BASE_URL="http://localhost:8080/api/transactions"

# Check if application is running
echo "Checking application status..."
if ! curl -s "$BASE_URL/health" > /dev/null; then
    echo "❌ Application is not running, please start the application first: ./mvnw spring-boot:run"
    exit 1
fi
echo "✅ Application is running normally"
echo

# 1. Create transaction
echo "1. Creating transaction..."
TRANSACTION1=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "USD",
    "transactionType": "DEPOSIT",
    "description": "Initial deposit",
    "referenceNumber": "REF001"
  }')

TRANSACTION_ID1=$(echo $TRANSACTION1 | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "✅ Transaction created successfully, ID: $TRANSACTION_ID1"
echo

# 2. Create second transaction
echo "2. Creating second transaction..."
TRANSACTION2=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.25,
    "currency": "EUR",
    "transactionType": "WITHDRAWAL",
    "description": "ATM withdrawal",
    "referenceNumber": "REF002"
  }')

TRANSACTION_ID2=$(echo $TRANSACTION2 | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "✅ Transaction created successfully, ID: $TRANSACTION_ID2"
echo

# 3. Query single transaction
echo "3. Querying single transaction..."
curl -s "$BASE_URL/$TRANSACTION_ID1" | jq '.' 2>/dev/null || echo "Please install jq for formatted JSON output"
echo

# 4. Query paginated transaction list
echo "4. Querying transaction list (paginated)..."
curl -s "$BASE_URL?page=0&size=10" | jq '.' 2>/dev/null || echo "Please install jq for formatted JSON output"
echo

# 5. Update transaction
echo "5. Updating transaction..."
UPDATED_TRANSACTION=$(curl -s -X PUT "$BASE_URL/$TRANSACTION_ID1" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 150.75,
    "currency": "USD",
    "transactionType": "DEPOSIT",
    "description": "Updated deposit",
    "referenceNumber": "REF001_UPDATED"
  }')

echo "✅ Transaction updated successfully"
echo $UPDATED_TRANSACTION | jq '.' 2>/dev/null || echo "Please install jq for formatted JSON output"
echo

# 6. Check if transaction exists
echo "6. Checking if transaction exists..."
EXISTS=$(curl -s "$BASE_URL/$TRANSACTION_ID1/exists")
echo "Transaction $TRANSACTION_ID1 exists: $EXISTS"
echo

# 7. Delete transaction
echo "7. Deleting transaction..."
DELETE_RESPONSE=$(curl -s -w "%{http_code}" -X DELETE "$BASE_URL/$TRANSACTION_ID2")
if [ "$DELETE_RESPONSE" = "204" ]; then
    echo "✅ Transaction deleted successfully, ID: $TRANSACTION_ID2"
else
    echo "❌ Failed to delete transaction, HTTP status: $DELETE_RESPONSE"
fi
echo

# 8. Verify deletion
echo "8. Verifying deletion..."
EXISTS_AFTER_DELETE=$(curl -s "$BASE_URL/$TRANSACTION_ID2/exists")
echo "Transaction $TRANSACTION_ID2 exists after deletion: $EXISTS_AFTER_DELETE"
echo

# 9. Final transaction list
echo "9. Final transaction list..."
curl -s "$BASE_URL?page=0&size=10" | jq '.' 2>/dev/null || echo "Please install jq for formatted JSON output"
echo

echo "=========================================="
echo "  Demo completed successfully!"
echo "=========================================="
echo "API Documentation: http://localhost:8080/swagger-ui.html"
echo "Health Check: http://localhost:8080/actuator/health"
echo "Application Metrics: http://localhost:8080/actuator/metrics"

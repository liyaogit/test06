package com.hsbc.transaction.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 交易实体类
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
public class Transaction {

    private String id;

    @NotNull(message = "交易金额不能为空")
    @DecimalMin(value = "0.01", message = "交易金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "货币类型不能为空")
    @Pattern(regexp = "^[A-Z]{3}$", message = "货币类型必须是3位大写字母代码")
    private String currency;

    @NotBlank(message = "交易类型不能为空")
    private String transactionType;

    private LocalDateTime timestamp;

    @Size(max = 500, message = "描述信息不能超过500个字符")
    private String description;

    private String referenceNumber;

    /**
     * 默认构造函数
     */
    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 带参数的构造函数
     */
    public Transaction(BigDecimal amount, String currency, String transactionType, String description, String referenceNumber) {
        this();
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.description = description;
        this.referenceNumber = referenceNumber;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }
} 
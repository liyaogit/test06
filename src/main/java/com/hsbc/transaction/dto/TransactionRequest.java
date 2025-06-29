package com.hsbc.transaction.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 交易请求DTO
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
public class TransactionRequest {

    @NotNull(message = "交易金额不能为空")
    @DecimalMin(value = "0.01", message = "交易金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "货币类型不能为空")
    @Pattern(regexp = "^[A-Z]{3}$", message = "货币类型必须是3位大写字母代码")
    private String currency;

    @NotBlank(message = "交易类型不能为空")
    private String transactionType;

    @Size(max = 500, message = "描述信息不能超过500个字符")
    private String description;

    private String referenceNumber;

    // 默认构造函数
    public TransactionRequest() {}

    // 带参数的构造函数
    public TransactionRequest(BigDecimal amount, String currency, String transactionType, String description, String referenceNumber) {
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.description = description;
        this.referenceNumber = referenceNumber;
    }

    // Getters and Setters
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
    public String toString() {
        return "TransactionRequest{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", description='" + description + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }
} 
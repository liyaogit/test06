package com.hsbc.transaction.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Transaction Request DTO
 *
 * @author HSBC Development Team
 * @version 1.0.0
 */
public class TransactionRequest {

    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency type cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency type must be a 3-letter uppercase code")
    private String currency;

    @NotBlank(message = "Transaction type cannot be blank")
    private String transactionType;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private String referenceNumber;

    // Default constructor
    public TransactionRequest() {}

    // Constructor with parameters
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

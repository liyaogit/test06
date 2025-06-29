package com.hsbc.transaction.exception;

/**
 * 无效交易异常
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }

    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidTransactionException invalidAmount() {
        return new InvalidTransactionException("交易金额无效，必须大于0");
    }

    public static InvalidTransactionException invalidCurrency(String currency) {
        return new InvalidTransactionException("无效的货币类型: " + currency);
    }

    public static InvalidTransactionException invalidTransactionType(String type) {
        return new InvalidTransactionException("无效的交易类型: " + type);
    }
} 
package com.hsbc.transaction.exception;

/**
 * 交易未找到异常
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static TransactionNotFoundException withId(String id) {
        return new TransactionNotFoundException("交易记录未找到，ID: " + id);
    }
} 
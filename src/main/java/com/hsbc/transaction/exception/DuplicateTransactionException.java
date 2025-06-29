package com.hsbc.transaction.exception;

/**
 * 重复交易异常
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String message) {
        super(message);
    }

    public DuplicateTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DuplicateTransactionException withReferenceNumber(String referenceNumber) {
        return new DuplicateTransactionException("重复的交易参考编号: " + referenceNumber);
    }
} 
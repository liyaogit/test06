package com.hsbc.transaction.exception;

/**
 * Transaction Not Found Exception
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
        return new TransactionNotFoundException("Transaction record not found, ID: " + id);
    }
}

package com.hsbc.transaction.exception;

/**
 * Invalid Transaction Exception
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
        return new InvalidTransactionException("Invalid transaction amount, must be greater than 0");
    }

    public static InvalidTransactionException invalidCurrency(String currency) {
        return new InvalidTransactionException("Invalid currency type: " + currency);
    }

    public static InvalidTransactionException invalidTransactionType(String type) {
        return new InvalidTransactionException("Invalid transaction type: " + type);
    }
}

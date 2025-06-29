package com.hsbc.transaction.exception;

/**
 * Duplicate Transaction Exception
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
        return new DuplicateTransactionException("Duplicate transaction reference number: " + referenceNumber);
    }
}

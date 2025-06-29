package com.hsbc.transaction.repository;

import com.hsbc.transaction.model.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Transaction Repository Interface
 *
 * @author HSBC Development Team
 * @version 1.0.0
 */
public interface TransactionRepository {

    /**
     * Save transaction
     *
     * @param transaction transaction object
     * @return saved transaction object
     */
    Transaction save(Transaction transaction);

    /**
     * Find transaction by ID
     *
     * @param id transaction ID
     * @return transaction object
     */
    Optional<Transaction> findById(String id);

    /**
     * Find all transactions
     *
     * @return list of all transactions
     */
    List<Transaction> findAll();

    /**
     * Find transactions with pagination
     *
     * @param page page number (starting from 0)
     * @param size page size
     * @return list of transactions
     */
    List<Transaction> findAll(int page, int size);

    /**
     * Get total count of transactions
     *
     * @return total count of transactions
     */
    long count();

    /**
     * Find transaction by reference number
     *
     * @param referenceNumber reference number
     * @return transaction object
     */
    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    /**
     * Delete transaction
     *
     * @param id transaction ID
     * @return whether deletion was successful
     */
    boolean deleteById(String id);

    /**
     * Check if transaction exists
     *
     * @param id transaction ID
     * @return whether exists
     */
    boolean existsById(String id);

    /**
     * Check if reference number already exists
     *
     * @param referenceNumber reference number
     * @return whether exists
     */
    boolean existsByReferenceNumber(String referenceNumber);
}

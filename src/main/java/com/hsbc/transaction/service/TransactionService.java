package com.hsbc.transaction.service;

import com.hsbc.transaction.dto.PagedResponse;
import com.hsbc.transaction.dto.TransactionRequest;
import com.hsbc.transaction.dto.TransactionResponse;

/**
 * Transaction Service Interface
 *
 * @author HSBC Development Team
 * @version 1.0.0
 */
public interface TransactionService {

    /**
     * Create new transaction
     *
     * @param request transaction request
     * @return created transaction response
     */
    TransactionResponse createTransaction(TransactionRequest request);

    /**
     * Get transaction by ID
     *
     * @param id transaction ID
     * @return transaction response
     */
    TransactionResponse getTransactionById(String id);

    /**
     * Get paginated transaction list
     *
     * @param page page number (starting from 0)
     * @param size page size
     * @return paginated transaction response
     */
    PagedResponse<TransactionResponse> getTransactions(int page, int size);

    /**
     * Update transaction
     *
     * @param id transaction ID
     * @param request update request
     * @return updated transaction response
     */
    TransactionResponse updateTransaction(String id, TransactionRequest request);

    /**
     * Delete transaction
     *
     * @param id transaction ID
     */
    void deleteTransaction(String id);

    /**
     * Check if transaction exists
     *
     * @param id transaction ID
     * @return whether exists
     */
    boolean existsById(String id);
}

package com.hsbc.transaction.service.impl;

import com.hsbc.transaction.config.CacheConfig;
import com.hsbc.transaction.dto.PagedResponse;
import com.hsbc.transaction.dto.TransactionRequest;
import com.hsbc.transaction.dto.TransactionResponse;
import com.hsbc.transaction.exception.DuplicateTransactionException;
import com.hsbc.transaction.exception.InvalidTransactionException;
import com.hsbc.transaction.exception.TransactionNotFoundException;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transaction Service Implementation
 *
 * @author HSBC Development Team
 * @version 1.0.0
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    // Supported currency types
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of(
            "USD", "EUR", "GBP", "JPY", "CNY", "HKD", "SGD", "AUD", "CAD", "CHF"
    );

    // Supported transaction types
    private static final Set<String> SUPPORTED_TRANSACTION_TYPES = Set.of(
            "DEPOSIT", "WITHDRAWAL", "TRANSFER", "PAYMENT", "REFUND"
    );

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @CacheEvict(value = CacheConfig.TRANSACTION_LIST_CACHE, allEntries = true)
    public TransactionResponse createTransaction(TransactionRequest request) {
        logger.info("Starting to create transaction, request: {}", request);

        // Validate request
        validateTransactionRequest(request);

        // Check reference number is duplicated
        if (request.getReferenceNumber() != null &&
            !request.getReferenceNumber().trim().isEmpty() &&
            transactionRepository.existsByReferenceNumber(request.getReferenceNumber())) {
            throw DuplicateTransactionException.withReferenceNumber(request.getReferenceNumber());
        }

        // Create transaction object
        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getCurrency(),
                request.getTransactionType(),
                request.getDescription(),
                request.getReferenceNumber()
        );

        // Save transaction
        try {
            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("Transaction created successfully, ID: {}", savedTransaction.getId());
            return convertToResponse(savedTransaction);
        } catch (IllegalArgumentException e) {
            throw new DuplicateTransactionException("Failed to create transaction: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = CacheConfig.TRANSACTION_CACHE, key = "#id")
    public TransactionResponse getTransactionById(String id) {
        logger.debug("Querying transaction, ID: {}", id);

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction ID cannot be empty");
        }

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> TransactionNotFoundException.withId(id));

        return convertToResponse(transaction);
    }

    @Override
    @Cacheable(value = CacheConfig.TRANSACTION_LIST_CACHE, key = "'page:' + #page + ':size:' + #size")
    public PagedResponse<TransactionResponse> getTransactions(int page, int size) {
        logger.debug("Paginated querying transaction, page: {}，size: {}", page, size);

        // Validate page parameters
        if (page < 0) {
            throw new InvalidTransactionException("Page number cannot be less than 0");
        }
        if (size <= 0 || size > 100) {
            throw new InvalidTransactionException("Page size must be between 1-100");
        }

        List<Transaction> transactions = transactionRepository.findAll(page, size);
        long totalElements = transactionRepository.count();

        List<TransactionResponse> responseList = transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responseList, page, size, totalElements);
    }

    @Override
    @CacheEvict(value = {CacheConfig.TRANSACTION_CACHE, CacheConfig.TRANSACTION_LIST_CACHE},
                key = "#id", allEntries = true)
    public TransactionResponse updateTransaction(String id, TransactionRequest request) {
        logger.info("Starting to update transaction, ID: {}，request: {}", id, request);

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction ID cannot be empty");
        }

        // Validate request
        validateTransactionRequest(request);

        // Get existing transaction
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> TransactionNotFoundException.withId(id));

        // Check reference number is duplicated (except current transaction)
        if (request.getReferenceNumber() != null &&
            !request.getReferenceNumber().trim().isEmpty() &&
            !request.getReferenceNumber().equals(existingTransaction.getReferenceNumber()) &&
            transactionRepository.existsByReferenceNumber(request.getReferenceNumber())) {
            throw DuplicateTransactionException.withReferenceNumber(request.getReferenceNumber());
        }

        // Update transaction information
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setCurrency(request.getCurrency());
        existingTransaction.setTransactionType(request.getTransactionType());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setReferenceNumber(request.getReferenceNumber());

        // Save update
        try {
            Transaction updatedTransaction = transactionRepository.save(existingTransaction);
            logger.info("Transaction updated successfully, ID: {}", updatedTransaction.getId());
            return convertToResponse(updatedTransaction);
        } catch (IllegalArgumentException e) {
            throw new DuplicateTransactionException("Failed to update transaction: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = {CacheConfig.TRANSACTION_CACHE, CacheConfig.TRANSACTION_LIST_CACHE},
                key = "#id", allEntries = true)
    public void deleteTransaction(String id) {
        logger.info("Starting to delete transaction, ID: {}", id);

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction ID cannot be empty");
        }

        if (!transactionRepository.existsById(id)) {
            throw TransactionNotFoundException.withId(id);
        }

        boolean deleted = transactionRepository.deleteById(id);
        if (deleted) {
            logger.info("Transaction deleted successfully, ID: {}", id);
        } else {
            throw new RuntimeException("Failed to delete transaction, ID: " + id);
        }
    }

    @Override
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return transactionRepository.existsById(id);
    }

    /**
     * Validate transaction request
     */
    private void validateTransactionRequest(TransactionRequest request) {
        if (request == null) {
            throw new InvalidTransactionException("Transaction request cannot be empty");
        }

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw InvalidTransactionException.invalidAmount();
        }

        // Validate currency type
        if (request.getCurrency() == null ||
            request.getCurrency().trim().isEmpty() ||
            !SUPPORTED_CURRENCIES.contains(request.getCurrency().toUpperCase())) {
            throw InvalidTransactionException.invalidCurrency(request.getCurrency());
        }

        // Validate transaction type
        if (request.getTransactionType() == null ||
            request.getTransactionType().trim().isEmpty() ||
            !SUPPORTED_TRANSACTION_TYPES.contains(request.getTransactionType().toUpperCase())) {
            throw InvalidTransactionException.invalidTransactionType(request.getTransactionType());
        }
    }

    /**
     * Convert to response object
     */
    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType(),
                transaction.getTimestamp(),
                transaction.getDescription(),
                transaction.getReferenceNumber()
        );
    }
}

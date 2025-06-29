package com.hsbc.transaction.repository.impl;

import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory Transaction Repository Implementation
 *
 * @author HSBC Development Team
 * @version 1.0.0
 */
@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    private final Map<String, String> referenceNumberToId = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction object cannot be null");
        }

        // Check if reference number is duplicate (except for itself)
        if (transaction.getReferenceNumber() != null &&
            !transaction.getReferenceNumber().trim().isEmpty()) {
            String existingId = referenceNumberToId.get(transaction.getReferenceNumber());
            if (existingId != null && !existingId.equals(transaction.getId())) {
                throw new IllegalArgumentException("Reference number already exists: " + transaction.getReferenceNumber());
            }
        }

        // Save transaction
        transactions.put(transaction.getId(), transaction);

        // Update reference number index
        if (transaction.getReferenceNumber() != null &&
            !transaction.getReferenceNumber().trim().isEmpty()) {
            referenceNumberToId.put(transaction.getReferenceNumber(), transaction.getId());
        }

        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(transactions.get(id));
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values())
                .stream()
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp())) // Sort by time descending
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll(int page, int size) {
        if (page < 0 || size <= 0) {
            return Collections.emptyList();
        }

        List<Transaction> allTransactions = findAll();
        int start = page * size;
        int end = Math.min(start + size, allTransactions.size());

        if (start >= allTransactions.size()) {
            return Collections.emptyList();
        }

        return allTransactions.subList(start, end);
    }

    @Override
    public long count() {
        return transactions.size();
    }

    @Override
    public Optional<Transaction> findByReferenceNumber(String referenceNumber) {
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        String id = referenceNumberToId.get(referenceNumber);
        if (id != null) {
            return findById(id);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        Transaction transaction = transactions.remove(id);
        if (transaction != null) {
            // Remove reference number index
            if (transaction.getReferenceNumber() != null) {
                referenceNumberToId.remove(transaction.getReferenceNumber());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return transactions.containsKey(id);
    }

    @Override
    public boolean existsByReferenceNumber(String referenceNumber) {
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            return false;
        }
        return referenceNumberToId.containsKey(referenceNumber);
    }

    /**
     * Clear all data (for testing)
     */
    public void clear() {
        transactions.clear();
        referenceNumberToId.clear();
    }

    /**
     * Get current number of stored transactions
     */
    public int size() {
        return transactions.size();
    }
}

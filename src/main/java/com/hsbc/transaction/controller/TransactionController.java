package com.hsbc.transaction.controller;

import com.hsbc.transaction.dto.PagedResponse;
import com.hsbc.transaction.dto.TransactionRequest;
import com.hsbc.transaction.dto.TransactionResponse;
import com.hsbc.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Transaction Controller - RESTful API
 *
 * @author HSBC Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "HSBC Transaction Management System API")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Create new transaction
     */
    @PostMapping
    @Operation(summary = "Create Transaction", description = "Create a new financial transaction record")
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {

        logger.info("Received create transaction request: {}", request);
        TransactionResponse response = transactionService.createTransaction(request);
        logger.info("Transaction created successfully, ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Transaction", description = "Get transaction details by transaction ID")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable String id) {

        logger.debug("Querying transaction, ID: {}", id);
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get paginated transaction list
     */
    @GetMapping
    @Operation(summary = "Get Transaction List", description = "Get paginated transaction list")
    public ResponseEntity<PagedResponse<TransactionResponse>> getTransactions(
            @Parameter(description = "Page number (starting from 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        logger.debug("Paginated query transactions, page: {}, size: {}", page, size);
        PagedResponse<TransactionResponse> response = transactionService.getTransactions(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Update transaction
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Transaction", description = "Update existing transaction information")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody TransactionRequest request) {

        logger.info("Received update transaction request, ID: {}, request: {}", id, request);
        TransactionResponse response = transactionService.updateTransaction(id, request);
        logger.info("Transaction updated successfully, ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete transaction
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Transaction", description = "Delete transaction record by transaction ID")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable String id) {

        logger.info("Received delete transaction request, ID: {}", id);
        transactionService.deleteTransaction(id);
        logger.info("Transaction deleted successfully, ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if transaction exists
     */
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check Transaction Existence", description = "Check if transaction with specified ID exists")
    public ResponseEntity<Boolean> existsTransaction(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable String id) {

        logger.debug("Checking if transaction exists, ID: {}", id);
        boolean exists = transactionService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check if transaction management service is running normally")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Transaction Management System is running normally");
    }
}

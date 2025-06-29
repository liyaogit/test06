package com.hsbc.transaction.service;

import com.hsbc.transaction.dto.PagedResponse;
import com.hsbc.transaction.dto.TransactionRequest;
import com.hsbc.transaction.dto.TransactionResponse;
import com.hsbc.transaction.exception.DuplicateTransactionException;
import com.hsbc.transaction.exception.InvalidTransactionException;
import com.hsbc.transaction.exception.TransactionNotFoundException;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 交易服务测试类
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequest validRequest;
    private Transaction validTransaction;

    @BeforeEach
    void setUp() {
        validRequest = new TransactionRequest(
                new BigDecimal("100.00"),
                "USD",
                "DEPOSIT",
                "Test deposit",
                "REF001"
        );

        validTransaction = new Transaction(
                new BigDecimal("100.00"),
                "USD",
                "DEPOSIT",
                "Test deposit",
                "REF001"
        );
        validTransaction.setId("test-id");
        validTransaction.setTimestamp(LocalDateTime.now());
    }

    @Test
    void createTransaction_ValidRequest_ShouldReturnTransactionResponse() {
        // Given
        when(transactionRepository.existsByReferenceNumber("REF001")).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(validTransaction);

        // When
        TransactionResponse response = transactionService.createTransaction(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("test-id");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.getCurrency()).isEqualTo("USD");
        assertThat(response.getTransactionType()).isEqualTo("DEPOSIT");

        verify(transactionRepository).existsByReferenceNumber("REF001");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_DuplicateReferenceNumber_ShouldThrowException() {
        // Given
        when(transactionRepository.existsByReferenceNumber("REF001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(validRequest))
                .isInstanceOf(DuplicateTransactionException.class)
                .hasMessageContaining("REF001");

        verify(transactionRepository).existsByReferenceNumber("REF001");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_InvalidAmount_ShouldThrowException() {
        // Given
        TransactionRequest invalidRequest = new TransactionRequest(
                new BigDecimal("-10.00"),
                "USD",
                "DEPOSIT",
                "Test",
                "REF001"
        );

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(invalidRequest))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("交易金额无效");
    }

    @Test
    void createTransaction_InvalidCurrency_ShouldThrowException() {
        // Given
        TransactionRequest invalidRequest = new TransactionRequest(
                new BigDecimal("100.00"),
                "XXX",
                "DEPOSIT",
                "Test",
                "REF001"
        );

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(invalidRequest))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("无效的货币类型");
    }

    @Test
    void createTransaction_InvalidTransactionType_ShouldThrowException() {
        // Given
        TransactionRequest invalidRequest = new TransactionRequest(
                new BigDecimal("100.00"),
                "USD",
                "INVALID_TYPE",
                "Test",
                "REF001"
        );

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(invalidRequest))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("无效的交易类型");
    }

    @Test
    void getTransactionById_ExistingId_ShouldReturnTransaction() {
        // Given
        when(transactionRepository.findById("test-id")).thenReturn(Optional.of(validTransaction));

        // When
        TransactionResponse response = transactionService.getTransactionById("test-id");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("test-id");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("100.00"));

        verify(transactionRepository).findById("test-id");
    }

    @Test
    void getTransactionById_NonExistingId_ShouldThrowException() {
        // Given
        when(transactionRepository.findById("non-existing")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactionById("non-existing"))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("non-existing");

        verify(transactionRepository).findById("non-existing");
    }

    @Test
    void getTransactionById_EmptyId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactionById(""))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("交易ID不能为空");
    }

    @Test
    void getTransactions_ValidPageAndSize_ShouldReturnPagedResponse() {
        // Given
        when(transactionRepository.findAll(0, 10)).thenReturn(Arrays.asList(validTransaction));
        when(transactionRepository.count()).thenReturn(1L);

        // When
        PagedResponse<TransactionResponse> response = transactionService.getTransactions(0, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getTotalPages()).isEqualTo(1);

        verify(transactionRepository).findAll(0, 10);
        verify(transactionRepository).count();
    }

    @Test
    void getTransactions_InvalidPageSize_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactions(0, 0))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("页面大小必须在1-100之间");

        assertThatThrownBy(() -> transactionService.getTransactions(0, 101))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("页面大小必须在1-100之间");
    }

    @Test
    void getTransactions_InvalidPageNumber_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactions(-1, 10))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("页码不能小于0");
    }

    @Test
    void updateTransaction_ValidRequest_ShouldReturnUpdatedTransaction() {
        // Given
        when(transactionRepository.findById("test-id")).thenReturn(Optional.of(validTransaction));
        when(transactionRepository.existsByReferenceNumber("REF002")).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(validTransaction);

        TransactionRequest updateRequest = new TransactionRequest(
                new BigDecimal("200.00"),
                "EUR",
                "WITHDRAWAL",
                "Updated description",
                "REF002"
        );

        // When
        TransactionResponse response = transactionService.updateTransaction("test-id", updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(transactionRepository).findById("test-id");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_NonExistingId_ShouldThrowException() {
        // Given
        when(transactionRepository.findById("non-existing")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> transactionService.updateTransaction("non-existing", validRequest))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("non-existing");

        verify(transactionRepository).findById("non-existing");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deleteTransaction_ExistingId_ShouldDeleteSuccessfully() {
        // Given
        when(transactionRepository.existsById("test-id")).thenReturn(true);
        when(transactionRepository.deleteById("test-id")).thenReturn(true);

        // When
        transactionService.deleteTransaction("test-id");

        // Then
        verify(transactionRepository).existsById("test-id");
        verify(transactionRepository).deleteById("test-id");
    }

    @Test
    void deleteTransaction_NonExistingId_ShouldThrowException() {
        // Given
        when(transactionRepository.existsById("non-existing")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> transactionService.deleteTransaction("non-existing"))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("non-existing");

        verify(transactionRepository).existsById("non-existing");
        verify(transactionRepository, never()).deleteById(any());
    }

    @Test
    void existsById_ExistingId_ShouldReturnTrue() {
        // Given
        when(transactionRepository.existsById("test-id")).thenReturn(true);

        // When
        boolean exists = transactionService.existsById("test-id");

        // Then
        assertThat(exists).isTrue();
        verify(transactionRepository).existsById("test-id");
    }

    @Test
    void existsById_NonExistingId_ShouldReturnFalse() {
        // Given
        when(transactionRepository.existsById("non-existing")).thenReturn(false);

        // When
        boolean exists = transactionService.existsById("non-existing");

        // Then
        assertThat(exists).isFalse();
        verify(transactionRepository).existsById("non-existing");
    }
} 
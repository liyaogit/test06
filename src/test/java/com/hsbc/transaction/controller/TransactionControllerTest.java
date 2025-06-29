package com.hsbc.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsbc.transaction.dto.PagedResponse;
import com.hsbc.transaction.dto.TransactionRequest;
import com.hsbc.transaction.dto.TransactionResponse;
import com.hsbc.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 交易控制器测试类
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionRequest validRequest;
    private TransactionResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new TransactionRequest(
                new BigDecimal("100.00"),
                "USD",
                "DEPOSIT",
                "Test deposit",
                "REF001"
        );

        validResponse = new TransactionResponse(
                "test-id",
                new BigDecimal("100.00"),
                "USD",
                "DEPOSIT",
                LocalDateTime.now(),
                "Test deposit",
                "REF001"
        );
    }

    @Test
    void createTransaction_ValidRequest_ShouldReturnCreated() throws Exception {
        // Given
        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenReturn(validResponse);

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("test-id")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.transactionType", is("DEPOSIT")))
                .andExpect(jsonPath("$.description", is("Test deposit")))
                .andExpect(jsonPath("$.referenceNumber", is("REF001")));

        verify(transactionService).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void createTransaction_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given - 无效的请求（负金额）
        TransactionRequest invalidRequest = new TransactionRequest(
                new BigDecimal("-10.00"),
                "USD",
                "DEPOSIT",
                "Test",
                "REF001"
        );

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    void createTransaction_MissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // Given - 缺少必需字段
        TransactionRequest invalidRequest = new TransactionRequest();
        invalidRequest.setAmount(new BigDecimal("100.00"));
        // 缺少currency和transactionType

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    void getTransaction_ExistingId_ShouldReturnTransaction() throws Exception {
        // Given
        when(transactionService.getTransactionById("test-id")).thenReturn(validResponse);

        // When & Then
        mockMvc.perform(get("/api/transactions/test-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("test-id")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.currency", is("USD")));

        verify(transactionService).getTransactionById("test-id");
    }

    @Test
    void getTransactions_DefaultPagination_ShouldReturnPagedResponse() throws Exception {
        // Given
        PagedResponse<TransactionResponse> pagedResponse = new PagedResponse<>(
                Arrays.asList(validResponse),
                0,
                10,
                1L
        );
        when(transactionService.getTransactions(0, 10)).thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("test-id")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)));

        verify(transactionService).getTransactions(0, 10);
    }

    @Test
    void getTransactions_CustomPagination_ShouldReturnPagedResponse() throws Exception {
        // Given
        PagedResponse<TransactionResponse> pagedResponse = new PagedResponse<>(
                Arrays.asList(validResponse),
                1,
                5,
                6L
        );
        when(transactionService.getTransactions(1, 5)).thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.totalPages", is(2)));

        verify(transactionService).getTransactions(1, 5);
    }

    @Test
    void updateTransaction_ValidRequest_ShouldReturnUpdatedTransaction() throws Exception {
        // Given
        TransactionResponse updatedResponse = new TransactionResponse(
                "test-id",
                new BigDecimal("200.00"),
                "EUR",
                "WITHDRAWAL",
                LocalDateTime.now(),
                "Updated description",
                "REF002"
        );
        when(transactionService.updateTransaction(eq("test-id"), any(TransactionRequest.class)))
                .thenReturn(updatedResponse);

        TransactionRequest updateRequest = new TransactionRequest(
                new BigDecimal("200.00"),
                "EUR",
                "WITHDRAWAL",
                "Updated description",
                "REF002"
        );

        // When & Then
        mockMvc.perform(put("/api/transactions/test-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("test-id")))
                .andExpect(jsonPath("$.amount", is(200.00)))
                .andExpect(jsonPath("$.currency", is("EUR")))
                .andExpect(jsonPath("$.transactionType", is("WITHDRAWAL")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.referenceNumber", is("REF002")));

        verify(transactionService).updateTransaction(eq("test-id"), any(TransactionRequest.class));
    }

    @Test
    void deleteTransaction_ExistingId_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(transactionService).deleteTransaction("test-id");

        // When & Then
        mockMvc.perform(delete("/api/transactions/test-id"))
                .andExpect(status().isNoContent());

        verify(transactionService).deleteTransaction("test-id");
    }

    @Test
    void existsTransaction_ExistingId_ShouldReturnTrue() throws Exception {
        // Given
        when(transactionService.existsById("test-id")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/transactions/test-id/exists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(transactionService).existsById("test-id");
    }

    @Test
    void existsTransaction_NonExistingId_ShouldReturnFalse() throws Exception {
        // Given
        when(transactionService.existsById("non-existing")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/transactions/non-existing/exists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(transactionService).existsById("non-existing");
    }

    @Test
    void healthCheck_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/transactions/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("\"交易管理系统运行正常\""));
    }
} 
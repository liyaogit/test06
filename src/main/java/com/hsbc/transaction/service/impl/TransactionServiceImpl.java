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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 交易服务实现类
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    // 支持的货币类型
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of(
            "USD", "EUR", "GBP", "JPY", "CNY", "HKD", "SGD", "AUD", "CAD", "CHF"
    );

    // 支持的交易类型
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
        logger.info("开始创建交易，请求：{}", request);

        // 验证请求
        validateTransactionRequest(request);

        // 检查参考编号是否重复
        if (request.getReferenceNumber() != null && 
            !request.getReferenceNumber().trim().isEmpty() &&
            transactionRepository.existsByReferenceNumber(request.getReferenceNumber())) {
            throw DuplicateTransactionException.withReferenceNumber(request.getReferenceNumber());
        }

        // 创建交易对象
        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getCurrency(),
                request.getTransactionType(),
                request.getDescription(),
                request.getReferenceNumber()
        );

        // 保存交易
        try {
            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("交易创建成功，ID：{}", savedTransaction.getId());
            return convertToResponse(savedTransaction);
        } catch (IllegalArgumentException e) {
            throw new DuplicateTransactionException("创建交易失败：" + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = CacheConfig.TRANSACTION_CACHE, key = "#id")
    public TransactionResponse getTransactionById(String id) {
        logger.debug("查询交易，ID：{}", id);

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTransactionException("交易ID不能为空");
        }

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> TransactionNotFoundException.withId(id));

        return convertToResponse(transaction);
    }

    @Override
    @Cacheable(value = CacheConfig.TRANSACTION_LIST_CACHE, key = "'page:' + #page + ':size:' + #size")
    public PagedResponse<TransactionResponse> getTransactions(int page, int size) {
        logger.debug("分页查询交易，页码：{}，大小：{}", page, size);

        // 验证分页参数
        if (page < 0) {
            throw new InvalidTransactionException("页码不能小于0");
        }
        if (size <= 0 || size > 100) {
            throw new InvalidTransactionException("页面大小必须在1-100之间");
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
        logger.info("开始更新交易，ID：{}，请求：{}", id, request);

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTransactionException("交易ID不能为空");
        }

        // 验证请求
        validateTransactionRequest(request);

        // 获取现有交易
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> TransactionNotFoundException.withId(id));

        // 检查参考编号是否重复（除了当前交易）
        if (request.getReferenceNumber() != null && 
            !request.getReferenceNumber().trim().isEmpty() &&
            !request.getReferenceNumber().equals(existingTransaction.getReferenceNumber()) &&
            transactionRepository.existsByReferenceNumber(request.getReferenceNumber())) {
            throw DuplicateTransactionException.withReferenceNumber(request.getReferenceNumber());
        }

        // 更新交易信息
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setCurrency(request.getCurrency());
        existingTransaction.setTransactionType(request.getTransactionType());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setReferenceNumber(request.getReferenceNumber());

        // 保存更新
        try {
            Transaction updatedTransaction = transactionRepository.save(existingTransaction);
            logger.info("交易更新成功，ID：{}", updatedTransaction.getId());
            return convertToResponse(updatedTransaction);
        } catch (IllegalArgumentException e) {
            throw new DuplicateTransactionException("更新交易失败：" + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = {CacheConfig.TRANSACTION_CACHE, CacheConfig.TRANSACTION_LIST_CACHE}, 
                key = "#id", allEntries = true)
    public void deleteTransaction(String id) {
        logger.info("开始删除交易，ID：{}", id);

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTransactionException("交易ID不能为空");
        }

        if (!transactionRepository.existsById(id)) {
            throw TransactionNotFoundException.withId(id);
        }

        boolean deleted = transactionRepository.deleteById(id);
        if (deleted) {
            logger.info("交易删除成功，ID：{}", id);
        } else {
            throw new RuntimeException("删除交易失败，ID：" + id);
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
     * 验证交易请求
     */
    private void validateTransactionRequest(TransactionRequest request) {
        if (request == null) {
            throw new InvalidTransactionException("交易请求不能为空");
        }

        // 验证金额
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw InvalidTransactionException.invalidAmount();
        }

        // 验证货币类型
        if (request.getCurrency() == null || 
            request.getCurrency().trim().isEmpty() ||
            !SUPPORTED_CURRENCIES.contains(request.getCurrency().toUpperCase())) {
            throw InvalidTransactionException.invalidCurrency(request.getCurrency());
        }

        // 验证交易类型
        if (request.getTransactionType() == null || 
            request.getTransactionType().trim().isEmpty() ||
            !SUPPORTED_TRANSACTION_TYPES.contains(request.getTransactionType().toUpperCase())) {
            throw InvalidTransactionException.invalidTransactionType(request.getTransactionType());
        }
    }

    /**
     * 转换为响应对象
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
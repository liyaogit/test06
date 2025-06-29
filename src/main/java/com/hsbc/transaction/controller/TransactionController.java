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
 * 交易控制器 - RESTful API
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "交易管理", description = "汇丰银行交易管理系统 API")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 创建新交易
     */
    @PostMapping
    @Operation(summary = "创建交易", description = "创建一个新的金融交易记录")
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        
        logger.info("收到创建交易请求：{}", request);
        TransactionResponse response = transactionService.createTransaction(request);
        logger.info("交易创建成功，ID：{}", response.getId());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 根据ID获取交易
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取交易", description = "根据交易ID获取交易详情")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "交易ID", required = true)
            @PathVariable String id) {
        
        logger.debug("查询交易，ID：{}", id);
        TransactionResponse response = transactionService.getTransactionById(id);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取交易列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取交易列表", description = "分页获取交易列表")
    public ResponseEntity<PagedResponse<TransactionResponse>> getTransactions(
            @Parameter(description = "页码（从0开始）", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "页面大小（1-100）", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("分页查询交易，页码：{}，大小：{}", page, size);
        PagedResponse<TransactionResponse> response = transactionService.getTransactions(page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 更新交易
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新交易", description = "更新现有交易的信息")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @Parameter(description = "交易ID", required = true)
            @PathVariable String id,
            
            @Valid @RequestBody TransactionRequest request) {
        
        logger.info("收到更新交易请求，ID：{}，请求：{}", id, request);
        TransactionResponse response = transactionService.updateTransaction(id, request);
        logger.info("交易更新成功，ID：{}", response.getId());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除交易
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除交易", description = "根据交易ID删除交易记录")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "交易ID", required = true)
            @PathVariable String id) {
        
        logger.info("收到删除交易请求，ID：{}", id);
        transactionService.deleteTransaction(id);
        logger.info("交易删除成功，ID：{}", id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 检查交易是否存在
     */
    @GetMapping("/{id}/exists")
    @Operation(summary = "检查交易存在", description = "检查指定ID的交易是否存在")
    public ResponseEntity<Boolean> existsTransaction(
            @Parameter(description = "交易ID", required = true)
            @PathVariable String id) {
        
        logger.debug("检查交易是否存在，ID：{}", id);
        boolean exists = transactionService.existsById(id);
        
        return ResponseEntity.ok(exists);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查交易管理服务是否正常运行")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("交易管理系统运行正常");
    }
} 
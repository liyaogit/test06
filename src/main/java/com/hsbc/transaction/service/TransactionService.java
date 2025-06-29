package com.hsbc.transaction.service;

import com.hsbc.transaction.dto.PagedResponse;
import com.hsbc.transaction.dto.TransactionRequest;
import com.hsbc.transaction.dto.TransactionResponse;

/**
 * 交易服务接口
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
public interface TransactionService {

    /**
     * 创建新交易
     * 
     * @param request 交易请求
     * @return 创建的交易响应
     */
    TransactionResponse createTransaction(TransactionRequest request);

    /**
     * 根据ID获取交易
     * 
     * @param id 交易ID
     * @return 交易响应
     */
    TransactionResponse getTransactionById(String id);

    /**
     * 获取交易列表（分页）
     * 
     * @param page 页码（从0开始）
     * @param size 页面大小
     * @return 分页交易响应
     */
    PagedResponse<TransactionResponse> getTransactions(int page, int size);

    /**
     * 更新交易
     * 
     * @param id 交易ID
     * @param request 更新请求
     * @return 更新后的交易响应
     */
    TransactionResponse updateTransaction(String id, TransactionRequest request);

    /**
     * 删除交易
     * 
     * @param id 交易ID
     */
    void deleteTransaction(String id);

    /**
     * 检查交易是否存在
     * 
     * @param id 交易ID
     * @return 是否存在
     */
    boolean existsById(String id);
} 
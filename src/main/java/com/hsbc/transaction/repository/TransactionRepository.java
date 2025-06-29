package com.hsbc.transaction.repository;

import com.hsbc.transaction.model.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * 交易仓储接口
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
public interface TransactionRepository {

    /**
     * 保存交易
     * 
     * @param transaction 交易对象
     * @return 保存后的交易对象
     */
    Transaction save(Transaction transaction);

    /**
     * 根据ID查找交易
     * 
     * @param id 交易ID
     * @return 交易对象
     */
    Optional<Transaction> findById(String id);

    /**
     * 查找所有交易
     * 
     * @return 所有交易列表
     */
    List<Transaction> findAll();

    /**
     * 分页查询交易
     * 
     * @param page 页码（从0开始）
     * @param size 页面大小
     * @return 交易列表
     */
    List<Transaction> findAll(int page, int size);

    /**
     * 获取交易总数
     * 
     * @return 交易总数
     */
    long count();

    /**
     * 根据参考编号查找交易
     * 
     * @param referenceNumber 参考编号
     * @return 交易对象
     */
    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    /**
     * 删除交易
     * 
     * @param id 交易ID
     * @return 是否删除成功
     */
    boolean deleteById(String id);

    /**
     * 检查交易是否存在
     * 
     * @param id 交易ID
     * @return 是否存在
     */
    boolean existsById(String id);

    /**
     * 检查参考编号是否已存在
     * 
     * @param referenceNumber 参考编号
     * @return 是否存在
     */
    boolean existsByReferenceNumber(String referenceNumber);
} 
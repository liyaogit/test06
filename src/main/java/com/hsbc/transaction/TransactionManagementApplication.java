package com.hsbc.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 汇丰银行交易管理系统主启动类
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
public class TransactionManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementApplication.class, args);
    }
} 
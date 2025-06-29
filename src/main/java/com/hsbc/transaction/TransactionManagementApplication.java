package com.hsbc.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * HSBC Transaction Management System Main Application Class
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

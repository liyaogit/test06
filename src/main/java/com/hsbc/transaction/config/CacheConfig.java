package com.hsbc.transaction.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration Class
 *
 * @author HSBC Development Team
 * @version 1.0.0
 */
@Configuration
public class CacheConfig {

    /**
     * Configure Caffeine cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Maximum cache entries
                .maximumSize(1000)
                // Cache expiration time: expire 1 hour after write
                .expireAfterWrite(1, TimeUnit.HOURS)
                // Record cache hit rate statistics
                .recordStats());
        return cacheManager;
    }

    /**
     * Transaction cache name constants
     */
    public static final String TRANSACTION_CACHE = "transactions";
    public static final String TRANSACTION_LIST_CACHE = "transactionList";
}

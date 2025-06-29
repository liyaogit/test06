package com.hsbc.transaction.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
@Configuration
public class CacheConfig {

    /**
     * 配置Caffeine缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 最大缓存条目数
                .maximumSize(1000)
                // 缓存过期时间：写入后1小时过期
                .expireAfterWrite(1, TimeUnit.HOURS)
                // 统计缓存命中率
                .recordStats());
        return cacheManager;
    }

    /**
     * 交易缓存名称常量
     */
    public static final String TRANSACTION_CACHE = "transactions";
    public static final String TRANSACTION_LIST_CACHE = "transactionList";
} 
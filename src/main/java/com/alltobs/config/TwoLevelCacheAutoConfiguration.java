package com.alltobs.config;

import com.alltobs.core.TwoLevelCacheManager;
import com.alltobs.util.SpelKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 类 TwoLevelCacheAutoConfiguration
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
@Configuration
@AutoConfiguration
@EnableCaching
@RequiredArgsConstructor
@AutoConfigureBefore(CacheAutoConfiguration.class)
@EnableConfigurationProperties(TwoLevelCacheProperties.class)
public class TwoLevelCacheAutoConfiguration implements CachingConfigurer {

    private final TwoLevelCacheProperties properties;

    private final RedisCacheManager redisCacheManager;

    private final CacheConfigurationProvider cacheConfigurationProvider;

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                .maximumSize(1000));
        return caffeineCacheManager;
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new TwoLevelCacheManager(caffeineCacheManager(), redisCacheManager, properties, cacheConfigurationProvider);
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SpelKeyGenerator();
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new TwoLevelCacheResolver(cacheManager());
    }
}

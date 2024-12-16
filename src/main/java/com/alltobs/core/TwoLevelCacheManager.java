package com.alltobs.core;

import com.alltobs.config.CacheConfigurationProvider;
import com.alltobs.config.TwoLevelCacheProperties;
import com.alltobs.config.TwoLevelCacheResolver;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * 类 TwoLevelCacheManager
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
public class TwoLevelCacheManager implements CacheManager {
    private final CacheManager firstLevelCacheManager;
    private final CacheManager secondLevelCacheManager;
    private final TwoLevelCacheProperties properties;
    private final CacheConfigurationProvider configurationProvider;

    public TwoLevelCacheManager(@NonNull CacheManager firstLevelCacheManager,
                                @NonNull CacheManager secondLevelCacheManager,
                                @NonNull TwoLevelCacheProperties properties,
                                @NonNull CacheConfigurationProvider configurationProvider) {
        this.firstLevelCacheManager = firstLevelCacheManager;
        this.secondLevelCacheManager = secondLevelCacheManager;
        this.properties = properties;
        this.configurationProvider = configurationProvider;
    }

    @Override
    @Nullable
    public Cache getCache(@NonNull String name) {
        Cache secondLevelCache = secondLevelCacheManager.getCache(name);
        if (secondLevelCache == null) {
            return null;
        }

        Cache firstLevelCache = firstLevelCacheManager.getCache(name);

        // 尝试从TwoLevelCacheResolver中获取当前方法的定制配置
        TwoLevelCacheResolver.CustomCacheConfig customConfig = TwoLevelCacheResolver.pollCustomConfig();

        boolean useFirst = (customConfig != null) ? customConfig.isUseFirstLevel() : Boolean.TRUE.equals(configurationProvider.useFirstLevel(name));

        long ttl = (customConfig != null) ? customConfig.getTtl() : configurationProvider.getTtl(name);

        String prefix = properties.getKeyPrefix();

        return new TwoLevelCache(name, firstLevelCache, secondLevelCache, useFirst, ttl, prefix);
    }

    @Override
    @NonNull
    public Collection<String> getCacheNames() {
        return secondLevelCacheManager.getCacheNames();
    }
}

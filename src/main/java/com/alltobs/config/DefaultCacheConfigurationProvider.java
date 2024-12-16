package com.alltobs.config;

import lombok.RequiredArgsConstructor;

/**
 * 类 DefaultCacheConfigurationProvider
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
@RequiredArgsConstructor
public class DefaultCacheConfigurationProvider implements CacheConfigurationProvider {

    private final TwoLevelCacheProperties properties;

    @Override
    public Boolean useFirstLevel(String cacheName) {
        if (properties.getCaches() != null && properties.getCaches().containsKey(cacheName)) {
            return properties.getCaches().get(cacheName).getUseFirstLevel();
        }
        return null;
    }

    @Override
    public Long getTtl(String cacheName) {
        if (properties.getCaches() != null && properties.getCaches().containsKey(cacheName)) {
            return properties.getCaches().get(cacheName).getTtl();
        }
        return null;
    }
}

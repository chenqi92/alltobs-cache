package com.alltobs.config;


import org.springframework.lang.Nullable;

/**
 * 接口 CacheConfigurationProvider
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
public interface CacheConfigurationProvider {

    @Nullable
    Boolean useFirstLevel(String cacheName);

    @Nullable
    Long getTtl(String cacheName);
}

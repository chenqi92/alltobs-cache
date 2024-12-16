package com.alltobs.config;

import com.alltobs.cache.FffukCachePut;
import com.alltobs.cache.FffukCacheable;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 类 TwoLevelCacheResolver
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
public class TwoLevelCacheResolver implements CacheResolver {

    private final CacheManager cacheManager;

    // 使用ThreadLocal存储当前方法调用的自定义参数(useFirstLevel、ttl)，供TwoLevelCacheManager使用
    private static final ThreadLocal<CustomCacheConfig> METHOD_CUSTOM_CACHE_CONFIG = new ThreadLocal<>();

    public TwoLevelCacheResolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    @NonNull
    public Collection<? extends Cache> resolveCaches(@NonNull CacheOperationInvocationContext<?> context) {
        Method method = context.getMethod();
        FffukCacheable myCacheable = AnnotationUtils.findAnnotation(method, FffukCacheable.class);
        FffukCachePut myCachePut = AnnotationUtils.findAnnotation(method, FffukCachePut.class);

        boolean customConfigSet = false;
        if (myCacheable != null) {
            // 从myCacheable中取出useFirstLevel和ttl
            CustomCacheConfig cfg = new CustomCacheConfig(myCacheable.useFirstLevel(), myCacheable.ttl());
            METHOD_CUSTOM_CACHE_CONFIG.set(cfg);
            customConfigSet = true;
        } else if (myCachePut != null) {
            CustomCacheConfig cfg = new CustomCacheConfig(myCachePut.useFirstLevel(), myCachePut.ttl());
            METHOD_CUSTOM_CACHE_CONFIG.set(cfg);
            customConfigSet = true;
        }

        Collection<String> cacheNames = context.getOperation().getCacheNames();
        List<Cache> caches = new ArrayList<>();
        for (String name : cacheNames) {
            Cache c = cacheManager.getCache(name);
            if (c != null) {
                caches.add(c);
            }
        }

        // 在缓存操作完成后清理ThreadLocal(这里先不清理，等返回后在拦截器处理)
        // 由于两级缓存的获取是在本次调用中完成, 返回caches后立即清理可能导致CacheManager拿不到
        // 实际中可在AOP方法执行后清理ThreadLocal, 此处简单处理：在getCache后清理。

        // 为了确保清理，可在本resolver的拓展中保证后置清理，但这里需与调用链一致。
        // 简化：在本示例中, TwoLevelCacheManager.getCache 读完后立即清空ThreadLocal。

        return caches;
    }

    /**
     * 提供给TwoLevelCacheManager调用的方法，从ThreadLocal中获取配置并清除
     */
    @Nullable
    public static CustomCacheConfig pollCustomConfig() {
        CustomCacheConfig cfg = METHOD_CUSTOM_CACHE_CONFIG.get();
        if (cfg != null) {
            METHOD_CUSTOM_CACHE_CONFIG.remove();
        }
        return cfg;
    }

    @Getter
    public static class CustomCacheConfig {
        private final boolean useFirstLevel;
        private final long ttl;

        public CustomCacheConfig(boolean useFirstLevel, long ttl) {
            this.useFirstLevel = useFirstLevel;
            this.ttl = ttl;
        }

    }
}

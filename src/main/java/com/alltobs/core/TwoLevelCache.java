package com.alltobs.core;

import jakarta.annotation.Nullable;
import org.springframework.cache.Cache;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;

/**
 * 类 TwoLevelCache
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
public class TwoLevelCache implements Cache {

    private final String name;
    final Cache firstLevelCache;
    final Cache secondLevelCache;
    final boolean useFirstLevel;
    final long ttl;
    final String keyPrefix;

    public TwoLevelCache(@NonNull String name, @Nullable Cache firstLevelCache, @NonNull Cache secondLevelCache, boolean useFirstLevel, long ttl, @NonNull String keyPrefix) {
        this.name = name;
        this.firstLevelCache = firstLevelCache;
        this.secondLevelCache = secondLevelCache;
        this.useFirstLevel = useFirstLevel;
        this.ttl = ttl;
        this.keyPrefix = keyPrefix.isEmpty() ? "" : (keyPrefix + ":");
    }

    @Override
    @NonNull
    public String getName() {
        return this.name;
    }

    @Override
    @NonNull
    public Object getNativeCache() {
        return this.secondLevelCache.getNativeCache();
    }

    private Object wrapKey(@NonNull Object key) {
        return keyPrefix + key;
    }

    @Override
    @Nullable
    public ValueWrapper get(@NonNull Object key) {
        Object realKey = wrapKey(key);
        if (useFirstLevel && firstLevelCache != null) {
            ValueWrapper value = firstLevelCache.get(realKey);
            if (value != null) {
                return value;
            }
        }
        ValueWrapper value = secondLevelCache.get(realKey);
        if (value != null && useFirstLevel && firstLevelCache != null) {
            firstLevelCache.put(realKey, value.get());
        }
        return value;
    }

    @Override
    @Nullable
    public <T> T get(@NonNull Object key, @Nullable Class<T> type) {
        Object realKey = wrapKey(key);
        if (useFirstLevel && firstLevelCache != null) {
            T value = firstLevelCache.get(realKey, type);
            if (value != null) {
                return value;
            }
        }
        T value = secondLevelCache.get(realKey, type);
        if (value != null && useFirstLevel && firstLevelCache != null) {
            firstLevelCache.put(realKey, value);
        }
        return value;
    }

    @Override
    @Nullable
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        Object realKey = wrapKey(key);
        try {
            if (useFirstLevel && firstLevelCache != null) {
                T value = firstLevelCache.get(realKey, valueLoader);
                if (value != null) {
                    return value;
                }
            }
            T value = secondLevelCache.get(realKey, valueLoader);
            if (value != null && useFirstLevel && firstLevelCache != null) {
                firstLevelCache.put(realKey, value);
            }
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(realKey, valueLoader, e);
        }
    }

    @Override
    public void put(@NonNull Object key, @Nullable Object value) {
        Object realKey = wrapKey(key);
        if (useFirstLevel && firstLevelCache != null) {
            firstLevelCache.put(realKey, value);
        }
        secondLevelCache.put(realKey, value);
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(@NonNull Object key, @Nullable Object value) {
        Object realKey = wrapKey(key);
        ValueWrapper vw = secondLevelCache.putIfAbsent(realKey, value);
        if (useFirstLevel && firstLevelCache != null && vw == null) {
            firstLevelCache.put(realKey, value);
        }
        return vw;
    }

    @Override
    public void evict(@NonNull Object key) {
        Object realKey = wrapKey(key);
        secondLevelCache.evict(realKey);
        if (useFirstLevel && firstLevelCache != null) {
            firstLevelCache.evict(realKey);
        }
    }

    @Override
    public void clear() {
        secondLevelCache.clear();
        if (useFirstLevel && firstLevelCache != null) {
            firstLevelCache.clear();
        }
    }
}

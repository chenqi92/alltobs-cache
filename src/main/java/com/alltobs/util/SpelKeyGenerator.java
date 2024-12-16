package com.alltobs.util;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

/**
 * 类 SpelKeyGenerator
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
public class SpelKeyGenerator implements KeyGenerator {
    @Override
    @NonNull
    public Object generate(@NonNull Object target, @NonNull Method method, @NonNull Object... params) {
        return org.springframework.cache.interceptor.SimpleKeyGenerator.generateKey(params);
    }

}

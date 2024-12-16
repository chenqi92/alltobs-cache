package com.alltobs.cache;

import org.springframework.cache.annotation.CachePut;

import java.lang.annotation.*;

/**
 * 注解 FffukCachePut
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FffukCachePut {

    String[] cacheNames() default {};

    String key() default "";

    boolean useFirstLevel() default false;

    long ttl() default 0;
}

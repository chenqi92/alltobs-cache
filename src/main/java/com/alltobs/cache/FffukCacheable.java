package com.alltobs.cache;

import java.lang.annotation.*;

/**
 * 自定义缓存注解，主要用于设置是否开启一级缓存和设置ttl
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FffukCacheable {

    String[] cacheNames() default {};

    String key() default "";

    boolean useFirstLevel() default false;

    long ttl() default 0;
}

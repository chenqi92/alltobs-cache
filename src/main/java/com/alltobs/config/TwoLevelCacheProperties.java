package com.alltobs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 类 TwoLevelCacheProperties
 *
 * @author ChenQi
 * &#064;date 2024/12/16
 */
@Data
@ConfigurationProperties(prefix = "l2cache")
@Component
public class TwoLevelCacheProperties {

    private boolean defaultUseFirstLevel = false;
    private long defaultTtl = 0;
    private String keyPrefix = "";

    private Map<String, CacheConfig> caches;

    @Data
    public static class CacheConfig {
        private Boolean useFirstLevel;
        private Long ttl;
    }
}

package com.github.fanzezhen.demo.fun.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 查询服务
 */
@Slf4j
@Service
public class SearchService {
    private final Random random = new Random();

    @Cacheable(value = "search", key = "#id", cacheManager = "funHuToolCacheManager")
    public int search(int id) {
        log.info("search {}", id);
        return random.nextInt();
    }
    
    @CachePut(value = "search", key = "#id", cacheManager = "funHuToolCacheManager")
    public int put(int id, int data) {
        log.info("put {} {}", id, data);
        return data;
    }
    
    @CacheEvict(value = "search", key = "#id", cacheManager = "funHuToolCacheManager")
    public void evict(int id) {
        log.info("evict {}", id);
    }
}

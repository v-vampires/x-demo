package com.xx.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.xx.compress.TestObj;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Classname CacheManager
 * @Description cache 统一管理
 * @Date 2019/10/21 11:41
 * @Created by yifanli
 */
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    public static TCache<String, String> TEST_CACHE = newObjCache(Caffeine.newBuilder()
            .recordStats()
            .maximumSize(100000)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .removalListener(new CaffeineRemovalListener<>("LANDLORD_CACHE"))
            .build(), true, String.class, id -> String.valueOf(id));


    private static <K, V> TCache<K, V> newObjCache(Cache<K, V> cache, boolean compress, CacheContext<K, V> cacheContext){
        return compress ? new CompressedCache<K, V>(cache, RedisCache.getInstance(), cacheContext.getRedisKeyMappingFunction(), cacheContext.getClazz()) : new NormalCache<K, V>(cache, RedisCache.getInstance(), cacheContext.getRedisKeyMappingFunction(), cacheContext.getClazz());
    }

    private static <K, V> TCache<K, V> newObjCache(Cache<K, V> cache, boolean compress, Class<V> clazz, Function<K, String> keyMappingFunction){
            return compress ? new CompressedCache<K, V>(cache, RedisCache.getInstance(), keyMappingFunction, clazz) : new NormalCache<K, V>(cache, RedisCache.getInstance(), keyMappingFunction, clazz);
    }

    private static <K, V> TCache<K, V> newByteCache(Cache<K, byte[]> cache, boolean compress, Class<V> clazz, Function<K, String> keyMappingFunction){
        return compress ? new CompressedCache<K, V>(cache, RedisCache.getInstance(), keyMappingFunction, clazz) : new NormalCache<K, V>(cache, RedisCache.getInstance(), keyMappingFunction, clazz);
    }


    private interface CacheContext<K, V>{
        Class<V> getClazz();
        Function<K, String> getRedisKeyMappingFunction();
    }

    private static class CaffeineRemovalListener<K,V> implements RemovalListener<K, V>{

        private String cacheName;

        public CaffeineRemovalListener(String cacheName) {
            this.cacheName = cacheName;
        }

        @Override
        public void onRemoval(@Nullable K key, @Nullable V value, @NonNull RemovalCause cause) {
            String monitorK = cacheName + "_removed_by_" + cause.name();
            logger.info(monitorK);
        }
    }
}

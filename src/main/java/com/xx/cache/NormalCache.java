package com.xx.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @Classname NormalCache
 * @Date 2019/10/23 14:54
 * @Created by yifanli
 */
public class NormalCache<K, V> implements TCache<K, V> {

    private static int REDIS_EXPIRE_SECONDS = 24 * 3600;

    private Cache<K, V> cache;//存储的是JavaBean对象

    private RedisCache redisCache;

    private Class<V> clazz;

    private Function<K, String> redisKeyMappingFunction;

    public NormalCache(Cache cache, RedisCache redisCache, Function<K, String> redisKeyMappingFunction, Class<V> clazz) {
        this.cache = cache;
        this.redisCache = redisCache;
        this.redisKeyMappingFunction = redisKeyMappingFunction;
        this.clazz = clazz;

    }

    @Override
    public V getIfPresent(K key) {
        return (V) cache.getIfPresent(key);
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> mappingFunction) {
        Preconditions.checkNotNull(key, "key不能为null");
        Preconditions.checkNotNull(mappingFunction, "mappingFunction不能为null");
        return cache.get(key, k -> {
            String mappingKey = redisKeyMappingFunction.apply(k);
            byte[] redisBytes = redisCache.get(mappingKey.getBytes());
            if(redisBytes != null && redisBytes.length > 0){
                return bytesToObj(redisBytes, clazz);
            }
            V v = mappingFunction.apply(k);
            byte[] bytes = objToBytes(v);
            if(bytes == null || bytes.length == 0){
                return null;
            }
            redisCache.setex(mappingKey.getBytes(), REDIS_EXPIRE_SECONDS, bytes);
            return v;
        });
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys, Function<Iterable<K>, Map<K, V>> mappingFunction) {
        Preconditions.checkNotNull(keys, "keys不能为null");
        Preconditions.checkNotNull(mappingFunction, "mappingFunction不能为null");
        return cache.getAll(keys, ks -> {
            List<String> redisKeys = StreamSupport.stream(ks.spliterator(), false).map(redisKeyMappingFunction).collect(Collectors.toList());
            Map<String, byte[]> mget = redisCache.mget(redisKeys);
            Map<String, V> redisResult = mget.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey(), (e) -> bytesToObj(e.getValue(), clazz)));
            Map<K, V> retResult = Maps.newHashMap();
            List<K> functionKeys = Lists.newArrayList();
            for (K k : ks) {
                String mappingKey = redisKeyMappingFunction.apply(k);
                if(redisResult.get(mappingKey) != null){
                    retResult.put(k, redisResult.get(mappingKey));
                    continue;
                }
                functionKeys.add(k);
            }
            Map<K, V> functionResult = mappingFunction.apply(functionKeys);
            //set to redis
            List<byte[]> keyValues = Lists.newArrayList();
            for (Map.Entry<K, V> kvEntry : functionResult.entrySet()) {
                keyValues.add(redisKeyMappingFunction.apply(kvEntry.getKey()).getBytes());
                keyValues.add(objToBytes(kvEntry.getValue()));
            }
            redisCache.msetex(REDIS_EXPIRE_SECONDS, keyValues);
            //merge
            retResult.putAll(functionResult);
            return retResult;
        });
    }

    @Override
    public void invalidate(K key) {
        if(key == null) return;
        String mappingKey = redisKeyMappingFunction.apply(key);
        redisCache.del(mappingKey.getBytes());
        cache.invalidate(key);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }
}

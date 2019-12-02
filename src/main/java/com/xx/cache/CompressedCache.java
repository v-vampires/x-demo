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
 * @Classname CompressedCache
 * @Description 带压缩的缓存 相当于一个二级缓存，存储的是byte数组，每次都解压反序列化
 * 目标是计划存储全量数据用来替换掉redis缓存，尽量少请求redis,数据的来源应该是redis缓存
 * 但是解压和反序列化也会有一定性能损耗，如果损耗大则应该用NormalCache
 * @Date 2019/10/23 14:54
 * @Created by yifanli
 */
public class CompressedCache<K, V> implements TCache<K, V> {

    private static int REDIS_EXPIRE_SECONDS = 24 * 3600;

    private Cache<K, byte[]> cache;//存储的是压缩后的数据

    private Class<V> clazz;

    private RedisCache redisCache;

    private Function<K, String> redisKeyMappingFunction;


    public CompressedCache(Cache cache, RedisCache redisCache,  Function<K, String> redisKeyMappingFunction, Class<V> clazz) {
        this.cache = cache;
        this.redisCache = redisCache;
        this.clazz = clazz;
        this.redisKeyMappingFunction = redisKeyMappingFunction;
    }

    @Override
    public V getIfPresent(K key) {
        byte[] bytes = cache.getIfPresent(key);
        return bytesToObj(bytes, clazz);
    }

    /**
     *
     * @param key
     * @param mappingFunction 从数据库获取对象的函数
     * @return
     */
    @Override
    public V get(K key, Function<? super K, ? extends V> mappingFunction) {
        Preconditions.checkNotNull(key, "key不能为null");
        Preconditions.checkNotNull(mappingFunction, "mappingFunction不能为null");
        byte[] bytes = cache.get(key, k -> {
            String mappingKey = redisKeyMappingFunction.apply(k);
            byte[] redisBytes = redisCache.get(mappingKey.getBytes());
            if(redisBytes != null && redisBytes.length > 0){
                return redisBytes;
            }
            V v = mappingFunction.apply(k);
            byte[] compressBytes = objToBytes(v);
            if(compressBytes == null || compressBytes.length == 0){
                return null;
            }
            redisCache.setex(mappingKey.getBytes(), REDIS_EXPIRE_SECONDS, compressBytes);
            return compressBytes;
        });
        return bytesToObj(bytes, clazz);
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys, Function<Iterable<K>, Map<K, V>> mappingFunction) {
        Preconditions.checkNotNull(keys, "keys不能为null");
        Preconditions.checkNotNull(mappingFunction, "mappingFunction不能为null");
        Map<K, byte[]> cacheRet = cache.getAll(keys, ks -> {
            List<String> redisKeys = StreamSupport.stream(ks.spliterator(), false).map(redisKeyMappingFunction).collect(Collectors.toList());
            Map<String, byte[]> mget = redisCache.mget(redisKeys);
            List<K> functionKeys = Lists.newArrayList();
            Map<K, byte[]> retResult = Maps.newHashMap();
            for (K k : ks) {
                String mappingKey = redisKeyMappingFunction.apply(k);
                if(mget.get(mappingKey)!= null){//redis 存在
                    retResult.put(k, mget.get(mappingKey));
                    continue;
                }
                functionKeys.add(k);
            }
            //set to redis
            Map<K, V> functionResult = mappingFunction.apply(functionKeys);
            List<byte[]> keyValues = Lists.newArrayList();
            for (Map.Entry<K, V> kvEntry : functionResult.entrySet()) {
                keyValues.add(redisKeyMappingFunction.apply(kvEntry.getKey()).getBytes());
                byte[] bytes = objToBytes(kvEntry.getValue());
                keyValues.add(bytes);
                retResult.put(kvEntry.getKey(), bytes);
            }
            redisCache.msetex(REDIS_EXPIRE_SECONDS, keyValues);
            return retResult;
        });
        Map<K, V> ret = cacheRet.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey(), (e) -> bytesToObj(e.getValue(), clazz)));
        return ret;
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
        byte[] bytes = objToBytes(value);
        if(bytes == null || bytes.length == 0){
            return;
        }
        cache.put(key, bytes);
    }
}

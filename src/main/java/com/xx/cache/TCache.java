package com.xx.cache;


import com.xx.compress.Lz4Util;
import com.xx.compress.ProtostuffSerializer;

import java.util.Map;
import java.util.function.Function;

/**
 * @Classname LocalCache
 * @Description TODO
 * @Date 2019/10/23 14:50
 * @Created by yifanli
 */
public interface TCache<K, V> {

    V getIfPresent(K key);

    V get(K key, Function<? super K, ? extends V> mappingFunction);

    Map<K, V> getAll(Iterable<K> keys, Function<Iterable<K>, Map<K, V>> mappingFunction);

    void invalidate(K key);

    void put(K key, V value);


    default V bytesToObj(byte[] bytes, Class<V> clazz){
        if(bytes == null || bytes.length == 0) return null;
        byte[] srcBytes = Lz4Util.unCompress(bytes);
        if(srcBytes == null || srcBytes.length == 0) return null;
        V v = ProtostuffSerializer.getInstance().deserialize(srcBytes, clazz);
        return v;
    }

    default byte[] objToBytes(V v){
        if(v == null) return null;
        byte[] serialize = ProtostuffSerializer.getInstance().serialize(v);
        if(serialize == null || serialize.length == 0) return null;
        return Lz4Util.compress(serialize);
    }
}

package com.xx.cache;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.benmanes.caffeine.cache.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xx.compress.Lz4Util;
import com.xx.compress.ProtostuffSerializer;
import com.xx.compress.SnappyUtil;
import com.xx.compress.TestObj;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Classname CaffeineTest
 * @Description TODO
 * @Date 2019/10/29 10:54
 * @Created by yifanli
 */
public class CaffeineTest {

    private static Cache<Integer, String> cache = Caffeine.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            //.expireAfterWrite(10, TimeUnit.SECONDS)
            .removalListener((RemovalListener<Integer, String>) (key, value, cause) -> System.out.println(key + "remove by " + cause.name()))
            .build();

    public static void main(String[] args) throws InterruptedException {
        cache.put(1, "11");
        cache.put(2, "22");
        cache.put(3, "33");
        cache.put(4, "44");

        Map<Integer, String> all = cache.getAll(Lists.newArrayList(1, 2, 3, 5), new Function<Iterable<? extends Integer>, Map<Integer, String>>() {
            @Override
            public Map<Integer, String> apply(Iterable<? extends Integer> integers) {
                Map<Integer, String> ret = Maps.newHashMap();
                for (Integer integer : integers) {
                    System.out.println("key:" + integer);
                    String r = String.valueOf(integer) + "X";
                    ret.put(integer, r);
                }
                return ret;
            }
        });
        cache.estimatedSize();
        System.out.println(all);

        TestObj obj = new TestObj();
        System.out.println(RamUsageEstimator.sizeOf(obj));
        System.out.println(RamUsageEstimator.shallowSizeOf(obj));
        byte[] serialize = ProtostuffSerializer.getInstance().serialize(obj);
        System.out.println(serialize.length);
        byte[] lz4Bytes = Lz4Util.compress(serialize);
        byte[] snappyBytes = SnappyUtil.compress(serialize);
        System.out.println("对象大小:" + RamUsageEstimator.sizeOf(obj) + ",序列化后:" + RamUsageEstimator.sizeOf(serialize) + ",lz4:" + RamUsageEstimator.sizeOf(lz4Bytes) + ",snappy:" + RamUsageEstimator.sizeOf(snappyBytes));

        Map<Integer, String> s = new HashMap<>();
        s.put(1, null);
        Object a = 100;
        System.out.println(a instanceof BigDecimal);
    }
}

package com.xx.compress;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Classname CompressClient
 * @Description TODO
 * @Date 2019/10/18 17:12
 * @Created by yifanli
 */
public class CompressClient {

    public static void main(String[] args) throws IOException {
        File file = new File("src/main/java/com/xx/compress/diff1.txt");
        FileInputStream fis = new FileInputStream(file);
        FileChannel channel = fis.getChannel();
        ByteBuffer bb = ByteBuffer.allocate((int) channel.size());
        channel.read(bb);
        byte[] beforeBytes = bb.array();

        System.out.println("压缩前大小：" + beforeBytes.length + " bytes");

        process(beforeBytes, "snappy", bytes -> SnappyUtil.compress(bytes), bytes -> SnappyUtil.unCompress(bytes));
        process(beforeBytes, "lz4", bytes -> Lz4Util.compress(bytes), bytes -> Lz4Util.unCompress(bytes));
        System.out.println("---------------------");

        TestObj obj = new TestObj();

        byte[] serialize = ProtostuffSerializer.getInstance().serialize(obj);
        System.out.println("压缩前大小：" + serialize.length + " bytes");
        process(serialize, "snappy", bytes -> SnappyUtil.compress(bytes), bytes -> SnappyUtil.unCompress(bytes));
        process(serialize, "lz4", bytes -> Lz4Util.compress(bytes), bytes -> Lz4Util.unCompress(bytes));

        Map<String, TestObj> map = new HashMap<>();
        map.put("111", obj);
        long s = System.nanoTime();
        for (int i = 0; i < 10000; i++) {

            map.get("111");
        }
        System.out.println("no compress:" + (System.nanoTime() - s));
        byte[] ser = ProtostuffSerializer.getInstance().serialize(obj);
        byte[] compress = Lz4Util.compress(ser);
        Map<String, byte[]> m = new HashMap<>();
        m.put("111", ser);
        long s1 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {

            byte[] bytes = m.get("111");
            //byte[] bytes1 = Lz4Util.unCompress(bytes);
            TestObj deserialize = ProtostuffSerializer.getInstance().deserialize(bytes, TestObj.class);
        }
        System.out.println("compress:" + (System.nanoTime() - s1));
    }


    public static void process(byte[] beforeBytes, String type, Function<byte[], byte[]> compressConsumer, Function<byte[], byte[]> unCompressConsumer) {
        long start1 = System.currentTimeMillis();
        byte[] compressBytes = compressConsumer.apply(beforeBytes);
        long end1 = System.currentTimeMillis();
        System.out.println(type + " 压缩后大小：" + compressBytes.length + " bytes, 压缩时间：" + (end1 - start1) + "ms");

        long start2 = System.currentTimeMillis();
        byte[] unCompressBytes = unCompressConsumer.apply(compressBytes);
        long end2 = System.currentTimeMillis();
        System.out.println(type + " 解压缩后大小：" + unCompressBytes.length + " bytes, 解压缩时间：" + (end2 - start2) + "ms");
    }
}

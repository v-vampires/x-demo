package com.xx.compress;

import net.jpountz.lz4.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Classname Lz4Util
 * @Description TODO
 * @Date 2019/10/18 17:37
 * @Created by yifanli
 */
public class Lz4Util {

    private static final byte[] EMPTY_BYTES = new byte[0];

    public static final int LZ4_BUFFER = 10240;

    public static byte[] compress(byte[] srcBytes){
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        LZ4Compressor compressor = factory.fastCompressor();
        LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(byteOutput, LZ4_BUFFER, compressor);
        try {
            compressedOutput.write(srcBytes);
            compressedOutput.close();
            return byteOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EMPTY_BYTES;
    }

    public static byte[] unCompress(byte[] bytes){
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(LZ4_BUFFER);
        LZ4FastDecompressor lz4FastDecompressor = factory.fastDecompressor();
        LZ4BlockInputStream lzis = new LZ4BlockInputStream(new ByteArrayInputStream(bytes), lz4FastDecompressor);
        try {
            int count;
            byte[] buffer = new byte[LZ4_BUFFER];
            while ((count = lzis.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
            lzis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}

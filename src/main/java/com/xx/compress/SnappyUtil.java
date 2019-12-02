package com.xx.compress;

import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * @Classname SnappyUtil
 * @Description TODO
 * @Date 2019/10/18 17:15
 * @Created by yifanli
 */
public class SnappyUtil {

    private static final byte[] EMPTY_BYTES = new byte[0];

    public static byte[] compress(byte[] srcBytes){
        try {
            return Snappy.compress(srcBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return EMPTY_BYTES;
        }
    }

    public static byte[] unCompress(byte[] bytes){
        try {
            return Snappy.uncompress(bytes);
        } catch (IOException e) {
            return EMPTY_BYTES;
        }
    }
}

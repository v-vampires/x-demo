package com.xx.cache;

import java.util.List;
import java.util.Map;

/**
 * @Classname RedisCache
 * @Description TODO
 * @Date 2019/10/25 17:41
 * @Created by yifanli
 */
public class RedisCache {

    //private Tedis tedis;

    private RedisCache() {
        /*Config config = ConfigService.getConfig("");
        String namespace = config.getProperty("redis.namespace", "");
        String password = config.getProperty("redis.password", "");
        String zkAddress = config.getProperty("redis.zkAddress", "");*/
        /*Properties properties = PropertiesPool.getPropertiesConfig(PropertiesPool.PropertiesName.CONFIG);
        String namespace = properties.getProperty("redis.namespace");
        String password = properties.getProperty("redis.password");
        String zkAddress = properties.getProperty("redis.zkAddress");
        this.tedis = new Tedis(namespace, password, zkAddress);*/
    }

    public static RedisCache getInstance(){
        return Holder.instance;
    }

    public byte[] get(byte[] key){
        //return tedis.get(key);
        return null;
    }

    public String setex(final byte[] key, final int seconds, final byte[] value){
        //return tedis.setex(key, seconds, value);
        return null;
    }

    public Long del(final byte[] key){
        //return tedis.del(key);
        return null;
    }

    public Map<String, byte[]> mget(List<String> keys){
        /*List<byte[]> byteKeys = keys.stream().map(k -> k.getBytes()).collect(Collectors.toList());
        Map<byte[], byte[]> r = tedis.mget(byteKeys.toArray(new byte[byteKeys.size()][]));
        Map<String, byte[]> m = r.entrySet().stream().collect(Collectors.toMap((e) -> new String(e.getKey()), (e) -> e.getValue()));
        return m;*/
        return null;
    }

    public String msetex(final int seconds, List<byte[]> keyValues){
        //return tedis.msetex(seconds, keyValues.toArray(new byte[keyValues.size()][]));
        return null;
    }

    private static class Holder {
        static RedisCache instance = new RedisCache();
    }

}

package com.xx.compress;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fitz.li on 2016/9/13.
 */
public class ProtostuffSerializer{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

    private ProtostuffSerializer(){}

    private static class ProtostuffSerializerHolder{
        private static final ProtostuffSerializer instance = new ProtostuffSerializer();
    }

    public static ProtostuffSerializer getInstance(){
        return ProtostuffSerializerHolder.instance;
    }


    public <T> byte[] serialize(T object) {
        if(object == null){
            return EMPTY_BYTES;
        }
        Class<T> clazz = (Class<T>) object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
            logger.error("序列化异常!", e);
        } finally {
            buffer.clear();
        }
        return EMPTY_BYTES;
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if(bytes == null || bytes.length <= 0) {
            return null;
        }
        try {
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            T obj = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
            return obj;
        } catch (Exception e) {
            logger.error("反序列化异常!", e);
        }
        return null;
    }



}

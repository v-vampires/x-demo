package com.xx.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xx.common.utils.Strings;

/**
 * @author huachen created on 11/21/13 2:07 PM
 * @version $Id$
 */
public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        SimpleModule module = new SimpleModule("DateTimeModule", Version.unknownVersion());
        /*module.addDeserializer(DateTime.class, new DateTimeDeserializer());
        module.addSerializer(DateTime.class, new DateTimeSerializer());*/
        SimpleModule shortDateModule = new SimpleModule("ShortDateModule", Version.unknownVersion());
        /*shortDateModule.addDeserializer(ShortDate.class, new ShortDateDeserializer());
        shortDateModule.addSerializer(ShortDate.class, new ShortDateSerializer());*/
        SimpleModule dateModule = new SimpleModule("DateModule", Version.unknownVersion());
        /*dateModule.addSerializer(Date.class, new DateSerializer());
        dateModule.addDeserializer(Date.class, new DateDeserializer());*/
        // null的字段不输出,减少数据量,也避免.NET系统用基本类型反序列化本系统的包装类型字段,导致出错
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(module);
        objectMapper.registerModule(shortDateModule);
        objectMapper.registerModule(dateModule);
    }

    public static ObjectMapper getObjectMapperInstance() {
        return objectMapper;
    }

    public static String writeObjectAsJson(Object obj) {
        try {
            if (obj == null) {
                //return StringUtil.EMPTY;
                return "";
            }
            return objectMapper.writeValueAsString(obj);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readJsonToBean(String json, Class<T> clazz) {
        try {
            if (Strings.isNullOrEmpty(json)) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readJsonToBean(String json, TypeReference<T> typeReference) {
        try {
            if (Strings.isNullOrEmpty(json)) {
                return null;
            }
            return objectMapper.readValue(json, typeReference);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
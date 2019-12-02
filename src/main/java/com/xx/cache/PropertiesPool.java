package com.xx.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Classname PropertiesPool
 * @Description TODO
 * @Date 2019/10/28 14:06
 * @Created by yifanli
 */
public class PropertiesPool {

    protected static Logger logger = LoggerFactory.getLogger(PropertiesPool.class);

    private static ConcurrentHashMap<PropertiesName, Properties> propertiesMap = new ConcurrentHashMap<>();

    public static Properties getPropertiesConfig(PropertiesName propertiesName){
        Properties properties = propertiesMap.get(propertiesName);
        if(properties != null){
            return properties;
        }
        try {
            //properties = PropertiesLoaderUtils.loadAllProperties(propertiesName.fileName);
            propertiesMap.put(propertiesName, properties);
        } catch (Exception e) {
            logger.error("load " + propertiesName.fileName + "has exception!", e);
            properties = new Properties();
        }
        return properties;
    }


    public enum PropertiesName {
        CONFIG("config.properties"),
        REDIS("redis.properties"),
        ;
        private String fileName;

        PropertiesName(String fileName) {
            this.fileName = fileName;
        }
    }
}

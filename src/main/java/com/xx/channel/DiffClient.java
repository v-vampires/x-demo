package com.xx.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.xx.common.http.Http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Classname DiffClient
 * @Description TODO
 * @Date 2019/10/11 18:54
 * @Created by yifanli
 */
public class DiffClient {
    public static void main(String[] args) throws IOException {

        File file = new File("src/main/java/com/xx/channel/diff1.txt");
        final AtomicInteger index = new AtomicInteger(0);
        Files.readLines(file, Charset.defaultCharset(), new LineProcessor<String>() {
            @Override
            public boolean processLine(String s) throws IOException {
                final StringBuilder url1 = new StringBuilder("http://172.31.22.190:8080/schedule/assistant/wholeDistributionChannel?");
                final StringBuilder url2 = new StringBuilder("http://172.31.39.122:8080/schedule/assistant/wholeDistributionChannel?");
                Map<String, String> map = Splitter.on(", ").withKeyValueSeparator(":").split(s);
                if (map.containsKey("houseId")) {
                    url1.append("unitId=").append(map.get("houseId"));
                    url2.append("unitId=").append(map.get("houseId"));
                    boolean isProduct = map.containsKey("productId");
                    if (isProduct) {
                        url1.append("&productId=").append(map.get("productId"));
                        url2.append("&productId=").append(map.get("productId"));
                    }
                    String result1 = Http.get(url1.toString()).request();
                    result1 = parse(result1, isProduct);
                    String result2 = Http.get(url2.toString()).request();
                    result2 = parse(result2, isProduct);
                    index.incrementAndGet();
                    if(!result1.equals(result2)){
                        System.out.println("i: " + index.get());
                        System.out.println(result1);
                        System.out.println(result2);
                    }else{
                        System.out.println("i: " + index.get());
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }


            @Override
            public String getResult() {
                return null;
            }
        });
    }

    public static String parse(String content, boolean isProduct){
        JSONObject jsonObject = JSON.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject result = isProduct ? data.getJSONObject("productCalResult") : data.getJSONObject("houseCalResult");
        return result.toJSONString();
    }
}

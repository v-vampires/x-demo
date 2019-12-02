package com.xx.jmx;

import com.xx.compress.TestObj;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname JMXTools
 * @Description TODO
 * @Date 2019/10/25 14:37
 * @Created by yifanli
 */
public class JMXTools {

    private static List<TestObj> list = new ArrayList<>();

    public static String monitorMemory(){
        StringBuilder sb = new StringBuilder("Memory:");
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage hmu = memoryMXBean.getHeapMemoryUsage();
        sb.append("[HeapMemoryUsage:");
        sb.append(" Used=" + hmu.getUsed() + " byte");
        sb.append(", Committed=" + hmu.getCommitted() + " byte");
        sb.append("]");

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        sb.append(", [NonHeapMemoryUsage:");
        sb.append(" Used=" + nonHeapMemoryUsage.getUsed() + " byte");
        sb.append(", Committed=" + nonHeapMemoryUsage.getCommitted() + " byte");
        sb.append("]");

        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(monitorMemory());
        while(true){
            for (int i = 0; i < 10; i++) {
                list.add(new TestObj());
            }
            System.out.println(monitorMemory());
            System.out.println("==============");
            Thread.sleep(500);
        }
    }
}

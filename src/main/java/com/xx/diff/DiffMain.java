package com.xx.diff;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.inclusion.Inclusion;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import de.danielbechler.diff.path.NodePath;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;

/**
 * @Classname DiffMain
 * @Description TODO
 * @Date 2019/11/20 16:50
 * @Created by yifanli
 */
public class DiffMain {

    /**
     * 反斜杠
     */
    private static String BACKSLASH = "/";
    /**
     * 替换 反斜杠 符号，默认使用 '.'
     */
    private static String POINT = ".";
    /**
     * 替换第一个 反斜杠 的符号，默认使用 空值
     */
    private static String EMPTY = "";

    public static void main(String[] args) {
       /* Map<String, String> working = Collections.singletonMap("item", "foo");
        Map<String, String> base = Collections.singletonMap("item", "bar");*/
        DiffObj base = new DiffObj("d1", new DiffSubObj("ds11", "ds12"));
        DiffObj working = new DiffObj("w1", new DiffSubObj("ws11", "ws12"));

        DiffNode diff = ObjectDifferBuilder.buildDefault().compare(working, base);
        System.out.println(diff.hasChanges());
        Map<String, DiffValues> stringDiffValuesMap = generateDiffMap(base, working, EMPTY, POINT);
        System.out.println(stringDiffValuesMap);

        BigDecimal b1 = new BigDecimal("10.0");
        BigDecimal b2 = new BigDecimal("10");
        System.out.println(b1.compareTo(b2) == 0);


    }

    public static Map<String, DiffValues> generateDiffMap(Object base, Object working, String replaceFirstBackslash, String replaceAllBackslash) {
        try {
            DiffNode root = ObjectDifferBuilder.buildDefault().compare(working, base);

            String finalReplaceFirstBackslash = Strings.isNullOrEmpty(replaceFirstBackslash) ? EMPTY : replaceFirstBackslash;
            String finalReplaceAllBackslash = Strings.isNullOrEmpty(replaceAllBackslash) ? POINT : replaceAllBackslash;

            Map<String, DiffValues> diffValuesMap = Maps.newHashMap();
            root.visit(
                    (diffNode, visit) -> {
                        if (diffNode.hasChanges() && !diffNode.hasChildren()) {
                            diffValuesMap.put(
                                    diffNode.getPath().toString().replaceFirst(BACKSLASH, finalReplaceFirstBackslash).replace(BACKSLASH, finalReplaceAllBackslash),
                                    new DiffValues(diffNode.canonicalGet(base), diffNode.canonicalGet(working)));
                        }
                    }
            );
            return diffValuesMap;
        } catch (Exception e) {
            e.printStackTrace();
            return Maps.newHashMap();
        }


    }




    public static class DiffValues {

        private Object oldValue;

        private Object newValue;

        public DiffValues(){}

        /**
         *
         * @param oldValue 旧值
         * @param newValue 新值
         */
        public DiffValues(Object oldValue, Object newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public void setOldValue(Object oldValue) {
            this.oldValue = oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        public void setNewValue(Object newValue) {
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            return "DiffValues{" +
                    "oldValue=" + oldValue +
                    ", newValue=" + newValue +
                    '}';
        }
    }
}

package com.xx.diff;

import java.io.Serializable;

/**
 * @Classname DiffSubObj
 * @Description TODO
 * @Date 2019/11/20 16:56
 * @Created by yifanli
 */
public class DiffSubObj implements Serializable {

    private String subF1;
    private String subF2;

    public DiffSubObj(String subF1, String subF2) {
        this.subF1 = subF1;
        this.subF2 = subF2;
    }

    public String getSubF1() {
        return subF1;
    }

    public void setSubF1(String subF1) {
        this.subF1 = subF1;
    }

    public String getSubF2() {
        return subF2;
    }

    public void setSubF2(String subF2) {
        this.subF2 = subF2;
    }
}

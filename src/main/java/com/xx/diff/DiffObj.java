package com.xx.diff;

/**
 * @Classname DiffObj 要有set、get方法
 * @Description TODO
 * @Date 2019/11/20 16:55
 * @Created by yifanli
 */
public class DiffObj {

    private String ff;

    private DiffSubObj subObj;

    public DiffObj(String ff, DiffSubObj subObj) {
        this.ff = ff;
        this.subObj = subObj;
    }

    public String getFf() {
        return ff;
    }

    public void setFf(String ff) {
        this.ff = ff;
    }

    public DiffSubObj getSubObj() {
        return subObj;
    }

    public void setSubObj(DiffSubObj subObj) {
        this.subObj = subObj;
    }
}

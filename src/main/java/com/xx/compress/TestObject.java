package com.xx.compress;


import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Classname TestObject
 * @Description TODO
 * @Date 2019/10/18 19:20
 * @Created by yifanli
 */
public class TestObject implements Serializable {
    private static final long serialVersionUID = 6855882782496027376L;

    private Random r = new Random();

    private long f1;
    private long f2;
    private long f3;
    private long f4;
    private long f5;
    private long f6;
    private long f7;
    private long f8;
    private long f9;
    private long f10;

    private String f21;
    private String f22;
    private String f23;
    private String f24;
    private String f25;
    private String f26;
    private String f27;
    private String f28;
    private String f29;
    private String f30;
    private Date date;

    public TestObject() {
        this.f1 = r.nextInt(10000000);
        this.f2 = r.nextInt(10000000);
        this.f3 = r.nextInt(10000000);
        this.f4 = r.nextInt(10000000);
        this.f5 = r.nextInt(10000000);
        this.f6 = r.nextInt(10000000);
        this.f7 = r.nextInt(10000000);
        this.f8 = r.nextInt(10000000);
        this.f9 = r.nextInt(10000000);
        this.f10 = r.nextInt(10000000);
        this.f21 = String.valueOf(r.nextInt(100000));
        this.f22 = String.valueOf(r.nextInt(100000));
        this.f23 = String.valueOf(r.nextInt(100000));
        this.f24 = String.valueOf(r.nextInt(100000));
        this.f25 = String.valueOf(r.nextInt(100000));
        this.f26 = String.valueOf(r.nextInt(100000));
        this.f27 = String.valueOf(r.nextInt(100000));
        this.f28 = String.valueOf(r.nextInt(100000));
        this.f29 = String.valueOf(r.nextInt(100000));
        this.f30 = String.valueOf(r.nextInt(100000));

        this.date = new Date();
    }
}

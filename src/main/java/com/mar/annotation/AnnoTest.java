package com.mar.annotation;

/**
 * @Author: 刘劲
 * @Date: 2020/4/12 11:02
 */
@MyService
public class AnnoTest {

    @MyAutoWired
    private MyAutoWiredTest val;

    public MyAutoWiredTest getVal() {
        return val;
    }
}

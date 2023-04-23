package com.itheima.reggie.common;

/**
 * 自定义异常类
 */
public class CustomExpection extends RuntimeException{
    public CustomExpection(String message) {
        super(message );
    }
}

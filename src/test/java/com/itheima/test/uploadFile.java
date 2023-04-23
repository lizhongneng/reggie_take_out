package com.itheima.test;

import org.junit.jupiter.api.Test;

public class uploadFile {
    @Test
    public void test1() {
        String fileName = "sdasd.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}

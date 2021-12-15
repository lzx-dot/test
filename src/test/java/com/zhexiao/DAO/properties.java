package com.zhexiao.DAO;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class properties {

    @Test
    public void test() throws IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("area.properties");

        Properties ps=new Properties();
        ps.load(is);
        System.out.println(ps.getProperty("6D"));

    }
}

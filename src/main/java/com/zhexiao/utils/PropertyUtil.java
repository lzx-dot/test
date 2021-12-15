package com.zhexiao.utils;

import lombok.ToString;

import java.io.*;
import java.util.Properties;

public class PropertyUtil {

    /**
     * 将property类中的内容输出到文件中
     * @param ps
     * @param filePath
     * @param append
     * @throws IOException
     */
    public static void write(Properties ps, String filePath ,Boolean append) throws IOException {
        //保存属性到b.properties文件
        FileOutputStream oFile = new FileOutputStream(filePath, append);//true表示追加打开,false每次都是清空再重写
        ps.store(new OutputStreamWriter(oFile, "utf-8"), "lll");
    }


}

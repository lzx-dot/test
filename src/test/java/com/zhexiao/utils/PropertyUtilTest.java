package com.zhexiao.utils;

import org.apache.ibatis.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class PropertyUtilTest {

    @Test
    public void test() throws IOException {
        psUser.setProperty("333","333");
        String path = this.getClass().getResource("/user.properties").getPath();
        System.out.println(path);
        PropertyUtil.write(psUser,"D:\\IDEA project\\server_01\\src\\main\\resources\\user.properties",false);

        String seatsStr = psSeats.getProperty("6D");
        String[] seats = seatsStr.split(";");
        System.out.println(seats.length);
        for (String str:seats){
            System.out.println(str);
        }
    }
    static Properties psArea;
    static Properties psUser;
    static Properties psSeats;
    static {
        InputStream isArea = null;
        InputStream isUser=null;
        InputStream isSeats_name_id=null;
        try {
            isArea = Resources.getResourceAsStream("area.properties");
            isUser=Resources.getResourceAsStream("user.properties");
            isSeats_name_id=Resources.getResourceAsStream("seats.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        psArea=new Properties();
        psUser=new Properties();
        psSeats=new Properties();
        try {
            psArea.load(isArea);
            psUser.load(isUser);
            psSeats.load(isSeats_name_id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        SimpleDateFormat hourSDF = new SimpleDateFormat("HH:mm:ss");

        String hour = hourSDF.format(new Date());
        Date date = new Date();
        long time = date.getTime();
        System.out.println((int) time%( (1000 * 60 * 60)) / (1000 * 60));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

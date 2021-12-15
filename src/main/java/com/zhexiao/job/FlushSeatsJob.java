package com.zhexiao.job;

import com.zhexiao.web.GrabSeatsServlet;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.quartz.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@DisallowConcurrentExecution
public class FlushSeatsJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String areaNo = dataMap.getString("areaNo");
        String segment = dataMap.getString("segment");
        String username = dataMap.getString("username");
        //要保持的位置
        String keep = dataMap.getString("keep");

        //选择任意座位
        String anySeats = dataMap.getString("anySeats");

        WebDriver driver = GrabSeatsServlet.driverMap.get(username);
        WebDriverWait webDriverWait = GrabSeatsServlet.waitMap.get(username);
        JavascriptExecutor jse = ((JavascriptExecutor) driver);

        SimpleDateFormat daySDF = new SimpleDateFormat("yyyy-M-d hh:mm:ss");
        String day = daySDF.format(new Date());
        System.out.println(day);
        System.out.println("--------刷新 ! ------------");
        //刷新页面
        driver.navigate().refresh();
        //等座位加载出来
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("seat")));
        /**
         * 寻找可用的座位，class=ava-idon
         */
        List<WebElement> seats = driver.findElements(By.className("ava-icon"));
        System.out.println("空座位：" + seats.size());

        if (anySeats!=null) {
            //如果可以预约任意座位
            System.out.println(username+"-预约任意座位");
            WebElement seat = seats.get(0);
            jse.executeScript("arguments[0].click();", seat);
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-dialog-autofocus")));
            WebElement ensure = driver.findElement(By.className("ui-dialog")).findElement(By.className("ui-dialog-autofocus"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ensure);
            /**
             * 任务已经完成
             */
            synchronized (GrabSeatsServlet.grabComplete.get(username)) {
                GrabSeatsServlet.grabComplete.put(username, true);
            }
            GrabSeatsServlet.seatsBooked.put(username, seat.getAttribute("data-no"));
            return;
        }

        //可以预约的座位Map，key-8224这种编号
        HashMap<String, WebElement> seatsMap = new HashMap<>();
        for (WebElement seat : seats) {
            seatsMap.put(seat.getAttribute("data-no"), seat);
        }
        if (keep != null) {
            System.out.println(username+"-preferSeats:" + keep);
            WebElement seat = seatsMap.get(keep);   //从空座位中选出要预约的座位来
            if (seat != null) {
                System.out.println("找到了对应的座位");
                jse.executeScript("arguments[0].click();", seat);
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-dialog-autofocus")));
                WebElement ensure = driver.findElement(By.className("ui-dialog")).findElement(By.className("ui-dialog-autofocus"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ensure);
                /**
                 * 任务已经完成
                 */
                synchronized (GrabSeatsServlet.grabComplete.get(username)) {
                    GrabSeatsServlet.grabComplete.put(username, true);
                }
//                GrabSeatsServlet.driverMap.get(username).close();  //预约成功了，关闭掉预约的窗口
                GrabSeatsServlet.seatsBooked.put(username, keep);
            }
        } else {
            /**
             * 从preferSeats中依次取出座位号，前面的座位号优先级高，找到了直接预约，然后退出
             */
            ArrayList<String> preferSeats = GrabSeatsServlet.userPreferMap.get(username);
            System.out.println(username+"-preferSeats:" + preferSeats);
            for (String seatNO : preferSeats) {
                WebElement seat = seatsMap.get(seatNO);
                if (seat != null) {
                    System.out.println("找到了对应的座位");
                    jse.executeScript("arguments[0].click();", seat);
                    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-dialog-autofocus")));
                    WebElement ensure = driver.findElement(By.className("ui-dialog")).findElement(By.className("ui-dialog-autofocus"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ensure);
                    /**
                     * 任务已经完成
                     */
                    synchronized (GrabSeatsServlet.grabComplete.get(username)) {
                        GrabSeatsServlet.grabComplete.put(username, true);
                    }

//                    GrabSeatsServlet.driverMap.get(username).close();  //预约成功了，关闭掉预约的窗口
                    GrabSeatsServlet.seatsBooked.put(username, seatNO);
                    break;
                }
            }
        }


    }

}

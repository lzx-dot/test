package com.zhexiao.web;

import com.alibaba.fastjson.JSONObject;

import com.zhexiao.DAO.areaDAO;
import com.zhexiao.DAO.userDAO;

import com.zhexiao.job.FlushSeatsJob;
import com.zhexiao.utils.MyBatisUtil;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class GrabSeatsServlet extends baseServlet {

    public static HashMap<String, WebDriver> driverMap = new HashMap<>();
    public static HashMap<String, WebDriverWait> waitMap = new HashMap<>();

    userDAO userDAO = MyBatisUtil.getSqlSession().getMapper(userDAO.class);
    areaDAO areaDAO = MyBatisUtil.getSqlSession().getMapper(areaDAO.class);

    static Properties psArea;
    static Properties psUser;
    static Properties psSeats_id;
    static Properties psSeats_name;
    static SchedulerFactory schedulerFactory;
    static Scheduler FlushSeatsScheduler;

    static {
        InputStream isArea = null;
        InputStream isUser = null;
        InputStream isSeats_name_id = null;
        InputStream isSeats_id_name = null;
        try {
            isArea = Resources.getResourceAsStream("area.properties");
            isUser = Resources.getResourceAsStream("user.properties");
            isSeats_name_id = Resources.getResourceAsStream("seat_name_id.properties");
            isSeats_id_name = Resources.getResourceAsStream("seat_id_name.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        psArea = new Properties();
        psUser = new Properties();
        psSeats_id = new Properties();
        psSeats_name = new Properties();
        try {
            psArea.load(isArea);
            psUser.load(isUser);
            psSeats_id.load(isSeats_name_id);
            psSeats_name.load(isSeats_id_name);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                isArea.close();
                isUser.close();
                isSeats_name_id.close();
                isSeats_id_name.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 1、创建调度器Scheduler
        schedulerFactory = new StdSchedulerFactory();
        try {
            FlushSeatsScheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录函数，登录之后跳转到预约请求界面
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        request.setAttribute("username", username);
        request.setAttribute("password", password);

        String path = this.getClass().getResource("/user.properties").getPath();
        System.out.println(path);

        if (!psUser.getProperty(username).equals(password)) {
            request.setAttribute("username", "登录失败");
            request.getRequestDispatcher("/login_success.jsp").forward(request, response);
            return;
        }

        ChromeOptions chromeOptions = new ChromeOptions();
        // 设置后台静默模式启动浏览器
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");

        // 优化参数
        HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_settings", 2);
        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.addArguments("blink-settings=imagesEnabled=false");//禁用图片
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");

//        System.setProperty("webdriver.chrome.driver", "D:\\Anaconda\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "/home/demo/chromedriver");

        WebDriver driver = null;
        WebDriverWait webDriverWait = null;
        if (driverMap.get(username) == null) {
            driver = new ChromeDriver(chromeOptions);
            webDriverWait = new WebDriverWait(driver, 10);
            driverMap.put(username, driver);
            System.out.println("driverMap的length:" + driverMap.size());
            waitMap.put(username, webDriverWait);
        }
        driver = driverMap.get(username);
        webDriverWait = waitMap.get(username);

        driver.get("https://ca.csu.edu.cn/authserver/login?service=http%3A%2F%2Flibzw.csu.edu.cn%2Fcas%2Findex.php%3Fcallback%3Dhttp%3A%2F%2Flibzw.csu.edu.cn%2Fhome%2Fweb%2Ff_second");

        if (isLogin(driver)) {
            //说明在登录界面
            System.out.println("当前在登录");
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);

            /**
             * 登录
             * 可以执行js代码，也可以
             */
//        jse.executeScript("document.getElementsByClassName(\"wid360\")[0].click();");
            driver.findElement(By.id("login_submit")).click();
        }


        /**
         * 获取cookie中的userid,username,token等信息
         */
        WebDriver.Options manage = driver.manage();
        Set<Cookie> cookiesSet = manage.getCookies();

        HttpSession session = request.getSession();   //将cookie存储在session中
        HashMap<String, String> cookies = new HashMap<>();
        for (Cookie c : cookiesSet) {
            cookies.put(c.getName(), c.getValue());
            session.setAttribute(c.getName(), c.getValue());
        }

        /**
         *只起到登录的作用，在下一个页面中选择区域，并且根据区域获得到segment信息
         */

        request.getRequestDispatcher("/book.jsp").forward(request, response);

//        grabSeats(request.getSession());

    }

    /**
     * 判断是否是登录界面
     *
     * @param driver
     * @return
     */
    public boolean isLogin(WebDriver driver) {
        boolean flag = true;
        try {
            driver.findElement(By.id("username")); //如果可以找到输入框，说明是登录界面
            System.out.println("找到了输入框");
        } catch (Exception e) {
            return false;
        }
        return flag;
    }

    /**
     * 用户偏好座位的HashMap,里面的ArrayList存放着座位的编号，如8224
     */
    public static HashMap<String, ArrayList<String>> userPreferMap = new HashMap<>();
    public static HashMap<String, Boolean> grabComplete = new HashMap<>();  //占座任务是否已经完成
    public static HashMap<String, String> seatsBooked = new HashMap<>();         //被用户预约的座位

    /**
     * 抢座位的方法
     * 要获取area
     *
     * @param request
     * @param response
     */
    public void grabSeats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        setArea(request, response);
        System.out.println("area=" + request.getParameter("area"));

        String areaNO = (String) request.getSession().getAttribute("areaNO");
        String segment = (String) request.getSession().getAttribute("segment");
        String username = (String) request.getSession().getAttribute("userid");   //username☞学号
        Long keepTime = Long.valueOf(request.getParameter("keepTime"));   //持续的时间

        seatsBooked.put(username, null);  //清楚上一次的预约记录

        Date date = new Date((System.currentTimeMillis() + keepTime * 60 * 1000));
        System.out.println("username=" + username);
        /**
         * 进入6楼d区域，空座位是按照时间来查询的，如果时间错了就不准（操蛋）
         */
        SimpleDateFormat daySDF = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat hourSDF = new SimpleDateFormat("HH:mm");
        String day = daySDF.format(new Date());
        String hour = hourSDF.format(new Date());
        /**
         * 获取segment
         */
        String six_D = "http://libzw.csu.edu.cn/web/seat3?area=" + areaNO + "&segment=" + segment + "&day=" + day + "&startTime=" + hour + "&endTime=22:00";

        /**
         * 跳转到新的页面中去
         */
        driverMap.get(username).get(six_D);

        /**
         * 要预约的座位
         */
        String anySeats = (String) request.getParameter("if_any");
        System.out.println("是否预约任意座位：" + anySeats);
        String keep = null;
        if (anySeats == null) {
            //选择任意座位
            keep = psSeats_id.getProperty("XF" + request.getParameter("area") + request.getParameter("keep"));
            if (keep == null) {
                Properties ps = new Properties();
                /**
                 * 偏好座位配置文件中写的是座位号：126;125这些
                 */
                InputStream is = Resources.getResourceAsStream(username + "/preference.properties");
                ps.load(is);
                String[] seatsNO = ps.getProperty(request.getParameter("area")).split(";");
                ArrayList<String> preferSeats = new ArrayList<>();
                for (String str : seatsNO) {
                    System.out.println(psSeats_id.getProperty("XF" + request.getParameter("area") + str));
                    //将座位的编号加入进去
                    preferSeats.add(psSeats_id.getProperty("XF" + request.getParameter("area") + str));
                }
                //将用户偏好座位加入到全局hashMap中
                userPreferMap.put(username, preferSeats);
                is.close();
            }

            System.out.println("keep=" + keep);
        }

        /****************************定时任务*********************************/

        grabComplete.put(username, false);//任务一开始没有完成

        // 1、创建调度器Scheduler

        // 2、创建JobDetail实例，并与FlushSeatsJob类绑定(Job执行内容)

        String flag = "true";
        JobDetail FlushSeatsJobDetail = (JobDetail) JobBuilder.newJob(FlushSeatsJob.class)
                .withIdentity(username, "group1")
                .usingJobData("username", username)
                .usingJobData("areaNO", areaNO)
                .usingJobData("segment", segment)
                .usingJobData("keep", keep)
                .usingJobData("anySeats", anySeats)
                .build();

        // 3、构建Trigger实例,每隔20s执行一次
        Trigger FlushSeatsTrigger = TriggerBuilder.newTrigger()
                .withIdentity(username, "triggerGroup1")
                .startNow()//立即生效
//                // 5s后停止
//                .endAt(new Date(System.currentTimeMillis() + 5 * 1000))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(1)//每隔20s执行一次
                        .repeatForever()).build();//一直执行

        //4、执行
        System.out.println("--------scheduler start ! ------------");

        FlushSeatsScheduler.scheduleJob(FlushSeatsJobDetail, FlushSeatsTrigger);
        FlushSeatsScheduler.start();

        /**
         * 在规定的时间内或者没有找到了座位，则一直循环着
         */
        while ((new Date()).getTime() < date.getTime()) {
            synchronized (grabComplete.get(username)) {
                if (grabComplete.get(username)) {
                    //当座位占到了或者终止占座任务时 grabComplete.get(username)为true
                    break;
                }
            }
        }
        //停止任务
        removeJob(username, "group1", username, "triggerGroup1");
        System.out.println("--------scheduler shutdown ! ------------");
        //获取各用户预约到的座位
        String seatNO = seatsBooked.get(username);
        if (seatNO == null) {
            //如果为空，说明没有预约到座位，跳转到预约座位的界面
            request.getRequestDispatcher("/book.jsp").forward(request, response);
        } else {
            String seat_name = (String) psSeats_name.get(seatNO);
            System.out.println("预约座位：" + seat_name + "成功");
            request.setAttribute("seat_name", seat_name);
            request.getRequestDispatcher("/book_success.jsp").forward(request, response);
        }
    }

    /**
     * 关闭占座的任务
     *
     * @param jobName
     * @param jobGroup
     * @param triggerName
     * @param triggerGroup
     * @throws SchedulerException
     */
    public void removeJob(String jobName, String jobGroup, String triggerName, String triggerGroup) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        Trigger trigger = FlushSeatsScheduler.getTrigger(triggerKey);
        if (trigger == null) {
            return;
        }
        // 停止触发器
        FlushSeatsScheduler.pauseTrigger(triggerKey);
        // 移除触发器
        FlushSeatsScheduler.unscheduleJob(triggerKey);
        // 删除任务
        FlushSeatsScheduler.deleteJob(jobKey);
    }

    /**
     * 停止占座的任务
     *
     * @param request
     * @param response
     */
    public void terminateTheJob(HttpServletRequest request, HttpServletResponse response) throws SchedulerException, IOException {
        String username = (String) request.getSession().getAttribute("userid");
        //停止该用户的占座任务
        synchronized (grabComplete.get(username)) {
            grabComplete.put(username, true);
        }
    }

    /**
     * 设置预约的区域
     *
     * @param request
     * @param response
     * @return
     */
    public void setArea(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("选择区域");
        String areaNO = psArea.getProperty(request.getParameter("area"));
        System.out.println("areaNO=" + areaNO);
        request.getSession().setAttribute("areaNO", areaNO);
        SimpleDateFormat daySDF = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat hourSDF = new SimpleDateFormat("HH:mm");
        String day = daySDF.format(new Date());
        String hour = hourSDF.format(new Date());
        try {
            setSegment(request.getSession(), day);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取座位空间的segment序列
     *
     * @param day
     * @return
     */
    public void setSegment(HttpSession session, String day) throws IOException {
        System.out.println("获取segment");
        String areaNO = (String) session.getAttribute("areaNO");
        String PHPSESSID = (String) session.getAttribute("PHPSESSID");
        String userid = (String) session.getAttribute("userid");
        String user_name = (String) session.getAttribute("user_name");
        String redirect_url = (String) session.getAttribute("redirect_url");
        String expire = (String) session.getAttribute("expire");
        String access_token = (String) session.getAttribute("access_token");

        String url = "http://libzw.csu.edu.cn/api.php/space_time_buckets?day=" + day + "&area=" + areaNO;
//        System.out.println(url);
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        URLConnection connection = realUrl.openConnection();
        // 设置通用的请求属性
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6,zh-TW;q=0.5");
        connection.setRequestProperty("Host", "libzw.csu.edu.cn");
        connection.setRequestProperty("Proxy-Connection", "keep-alive");
        connection.setRequestProperty("Referer", "http://libzw.csu.edu.cn/web/seat2/area/6/day/2021-5-10");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");

        connection.setRequestProperty("cookie",
                "_ga=GA1.3.66555017.1620403688; " +
                        "" + "PHPSESSID=" + PHPSESSID + ";" +
                        "" + "userid=" + userid + ";" +
                        "" + "user_name=" + user_name + ";" +
                        "" + "access_token=" + access_token + ";" +
                        "" + "expire=" + expire + ";" +
                        "" + "redirect_url=" + redirect_url
        );
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        connection.setRequestProperty("DNT", "1");
//        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//        connection.setRequestProperty("userid", userid);
//        connection.setRequestProperty("user_name", user_name);
//        connection.setRequestProperty("access_token", access_token);
//        connection.setRequestProperty("expire", expire);
//        connection.setRequestProperty("redirect_url", redirect_url);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        JSONObject jsonObject = JSONObject.parseObject(sb.toString());
        String dataStr = jsonObject.get("data").toString();
        in.close();

        /**
         * list是一个列表，里面存放着bookTimeID
         */
        String listStr = JSONObject.parseObject(dataStr).get("list").toString();

        //list的第一个元素
        String list_0 = JSONObject.parseArray(listStr).get(0).toString();
        //获取到segment
        String bookTimeId = JSONObject.parseObject(list_0).get("bookTimeId").toString();

        session.setAttribute("segment", bookTimeId);
        System.out.println("segment==" + bookTimeId);
    }

    /**
     * 关闭服务器时释放资源，
     */
    @SneakyThrows
    @Override
    public void destroy() {
        super.destroy();
        System.out.println();
        System.out.println();
        System.out.println("销毁所有driver");
        System.out.println();
        System.out.println();
        /**
         * 关闭时要将所有的driver全部关闭，放置浪费资源
         */
        for (String username : driverMap.keySet()) {
            driverMap.get(username).close();
        }
        FlushSeatsScheduler.shutdown();
    }
}

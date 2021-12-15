<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <meta charset="UTF-8">
    <title>预约界面</title>
    <link rel="stylesheet" href="css/book.css" type="text/css">
    <link rel="stylesheet" href="css/font-awesome.css" type="text/css">
    <link rel="stylesheet" href="css/modalBox.css" type="text/css">
    <link rel="stylesheet" href="css/time.css" type="text/css">

    <style>
        input::-webkit-input-placeholder {
            color: white;
        }

        input::-moz-placeholder {
            /* Mozilla Firefox 19+ */
            color: white;
        }

        input:-moz-placeholder {
            /* Mozilla Firefox 4 to 18 */
            color: white;
        }

        input:-ms-input-placeholder {
            /* Internet Explorer 10-11 */
            color: white;
        }
    </style>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <SCRIPT type="text/javascript">

        window.onload = function () {
            let width = document.documentElement.clientWidth;
            let height = document.documentElement.clientHeight;
            let body = document.getElementsByTagName("body")[0];
            body.style.width = width + "px";
            body.style.height = height + "px";


            /*建立模态框对象*/
            let modalBox = {};
            /*获取模态框*/
            modalBox.modal = document.getElementById("myModal");
            /*获得trigger按钮*/
            modalBox.triggerBtn = document.getElementById("triggerBtn");
            /*获得关闭按钮*/
            modalBox.closeBtn = document.getElementById("closeBtn");
            /*模态框显示*/
            modalBox.show = function (keepTime) {
                console.log(this.modal);
                this.modal.style.display = "block";
                var intDiff = parseInt(parseInt(keepTime) * 60);//倒计时总秒数量
                timer(intDiff);
            }
            /*模态框关闭*/
            modalBox.close = function () {
                this.modal.style.display = "none";
                clearInterval(sequence);
                //停止后台的占座任务
                let xmlHttpRequest = new XMLHttpRequest();
                xmlHttpRequest.open("GET", "http://39.99.139.67:8080/server_01_war/grabSeats?action=terminateTheJob");
                xmlHttpRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                xmlHttpRequest.send();
            }
            /*当用户双击模态框内容之外的区域，模态框也会关闭*/
            modalBox.outsideClick = function () {
                var modal = this.modal;
                window.ondblclick = function (event) {
                    if (event.target == modal) {
                        clearInterval(sequence);
                        modalBox.modal.style.display = "none";
                        //停止后台的占座任务
                        let xmlHttpRequest = new XMLHttpRequest();
                        xmlHttpRequest.open("GET", "http://39.99.139.67:8080/server_01_war/grabSeats?action=terminateTheJob");
                        xmlHttpRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                        xmlHttpRequest.send();

                    }
                }
            }
            /*模态框初始化*/
            modalBox.init = function () {
                var that = this;
                this.triggerBtn.onclick = function () {
                    //预约的时间
                    let keepTime = document.getElementById("keepTime").value;
                    that.show(keepTime);
                    // return false;
                }
                this.closeBtn.onclick = function () {
                    that.close();
                }
                this.outsideClick();
            }
            modalBox.init();


            let sequence = 0;

            document.getElementById("keepTime");

            /**
             * 倒计时函数，intDiff为倒计时的秒数
             * @param intDiff
             */
            function timer(intDiff) {
                // clearInterval(sequence);
                // sequence = window.setInterval(function () {
                //
                var day = 0,

                    hour = 0,

                    minute = 0,

                    second = 0;//时间默认值

                if (intDiff > 0) {

                    day = Math.floor(intDiff / (60 * 60 * 24));

                    hour = Math.floor(intDiff / (60 * 60)) - (day * 24);

                    minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);

                    second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);

                }

                if (minute <= 9) minute = '0' + minute;

                if (second <= 9) second = '0' + second;

                $('#day_show').html(day + "天");

                $('#hour_show').html('<s id="h"></s>' + hour + '时');

                $('#minute_show').html('<s></s>' + minute + '分');

                $('#second_show').html('<s></s>' + second + '秒');
                //
                //     intDiff--;
                //
                // }, 1000);

            }


            // $(function () {
            //
            //     var keepTime = document.getElementById("keepTime").getAttribute("value");
            //     var intDiff = parseInt(parseInt(keepTime)*60);//倒计时总秒数量
            //     timer(intDiff);
            //
            // });
        }

    </SCRIPT>

    <!--    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">-->

</head>

<body>
<form action="http://39.99.139.67:8080/server_01_war/grabSeats?action=grabSeats" method="POST">
    <div id="login-box">
        <h1>选择预约区域</h1>
        <div class="form">
            <div class="item">
                <p style="align-self: flex-start;color: #00ffff">选择区域</p>
                <div class="area"><input type="radio" name="area" value="6D" checked="checked"> 6D</div>
                <div class="area"><input type="radio" name="area" value="6E"> 6E</div>
                <div class="area"><input type="radio" name="area" value="6A"> 6A</div>
                <div class="area"><input type="radio" name="area" value="5A"> 5A</div>
                <div class="area"><input type="radio" name="area" value="3D"> 3D</div>
            </div>

            <div class="item">
                <p style="align-self: flex-start;color: #00ffff">预约任意位置</p>
                <div class="area"><input type="checkbox" name="if_any" value="true"> 是</div>
            </div>

            <div class="item">
                <p style="color: #0df4fc;align-self: flex-start">设置抢座的时间，默认为1分钟</p>
                <input type="text" value="1" name="keepTime" id="keepTime">
            </div>

            <div class="item">
                <p style="color: #fc0d21">如果要保持座位不被预约，选择上面的楼层,并在下面填写座位编号</p>
                <input type="text" placeholder="填写座位号" name="keep">
            </div>


        </div>
        <button id="triggerBtn">开始预约</button>
    </div>
</form>

<!-- 模态框 -->
<div id="myModal" class="modal">

    <div class="modal-content">
        <%
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            String dataStr = sdf.format(date);
        %>
        <div class="modal-body">
            <h3>正在为您预约座位，开始时间：<%=dataStr%></h3>
            <h1>设置的预约时长</h1>

            <div class="time-item">

                <span id="day_show">0天</span>

                <strong id="hour_show">0时</strong>

                <strong id="minute_show">0分</strong>

                <strong id="second_show">0秒</strong>

            </div>
        </div>
        <div class="modal-footer">
            <h3></h3>
        </div>

        <div class="modal-header">
            <span id="closeBtn" class="close">取消预约</span>
        </div>

    </div>
</div>

</body>

<footer>
    <div id="message-box">Hello, I'm JiadeChen!</div>
</footer>

</html>
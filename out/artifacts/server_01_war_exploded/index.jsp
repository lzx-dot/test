<%@ page import="java.net.InetAddress" %><%--
  Created by IntelliJ IDEA.
  User: ZX
  Date: 2021/5/11
  Time: 15:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html>
<head>
    <meta charset="UTF-8">
    <title>登录界面</title>
    <link rel="stylesheet" href="css/login.css" type="text/css">
    <link rel="stylesheet" href="css/font-awesome.css" type="text/css">

    <style>
        input::-webkit-input-placeholder {
            color:white;
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

    <SCRIPT type="text/javascript">

        window.onload=function (){
            let width = document.documentElement.clientWidth;
            let height = document.documentElement.clientHeight;
            let body = document.getElementsByTagName("body")[0];
            body.style.width=width+"px";
            body.style.height=height+"px";
        }

    </SCRIPT>
</head>
<%
    String ip= request.getLocalAddr();
%>

<body>
    <form action="http://39.99.139.67:8080/server_01_war/grabSeats?action=login" method="POST">
        <div id="login-box">
            <h1>Login</h1>
            <div class="form">
                <div class="item">
                    <i class="fa fa-user-circle" aria-hidden="true"></i>
                    <input type="text" placeholder="username" name="username">
                </div>

                <div class="item">
                    <i class="fa fa-key" aria-hidden="true"></i>
                    <input type="password" placeholder="password" name="password">
                </div>
            </div>
            <button type="submit">Login</button>
        </div>
    </form>
</body>

<footer>
    <div id="message-box">Hello, I'm JiadeChen!</div>
</footer>

</html>
<%-- 
    Document   : login
    Created on : Apr 1, 2025, 4:48:20 PM
    Author     : LAM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="assets/css/login.css"/>
    </head>
    <body>

        <div class="wrapper">
            <form action="MainController" method="post">
                <input type="hidden" name="action" value="login"/>
                <h1>Login</h1> 
                <div class="input-box">
                    <input type="text" name="txtUserID" placeholder="Enter username" required="required"/>
                </div>
                <div class="input-box">
                    <input type="password" name="txtPassword" placeholder="Enter password" required="required"/>
                </div>
                <div class="remember-forgot">
                    <input type="checkbox"/>Remember Me?
                    <a href="#">Forgot Password?</a>
                </div>
                <button class="btn" type="submit">Login</button>
                <div class="register-link">
                    <p>Don't have an account?</p>
                    <a href="#">Register</a>
                </div>
            </form>
        </div>
        <%
            String message = request.getAttribute("message") + "";
            message = message.equals("null") ? "" : message;
        %>

        <span style="color: red"><%=message%></span>
    </body>
</html>

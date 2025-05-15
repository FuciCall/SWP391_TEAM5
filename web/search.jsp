<%-- 
    Document   : search
    Created on : Apr 1, 2025, 4:58:33 PM
    Author     : LAM
--%>

<%@page import="utils.AuthUtils"%>
<%@page import="java.util.List"%>
<%@page import="dto.GameDTO"%>
<%@page import="dto.UserDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Main Page</title>
    </head>
    <body>
        <c:if test="${not empty sessionScope.user}">
            <h1>Welcome ${user.fullName} </h1>

            <c:set var="searchTerm" value="${requestScope.searchTerm == null ? '' : requestScope.searchTerm}" />

            <form action="MainController">
                <input type="hidden" name="action" value="logout"/>
                <input type="submit" value="Logout"/>
            </form>

            <form action="MainController">
                <input type="hidden" name="action" value="search"/>
                Search: <input type="text" name="searchTerm" value="${searchTerm}"/>
                <input type="submit" value="Search"/>
            </form>



            <c:set var="isAdmin" value="<%=AuthUtils.isAdmin(session)%>"/> 
            <c:if test="${isAdmin}">
                <a href="gameForm.jsp">
                    Add
                </a>
            </c:if>

            <c:if test="${not empty requestScope.games}">

                <table border="1">
                    <thead>
                        <tr>
                            
                            <th>ID</th>
                            <th>Image</th>
                            <th>Name</th>
                            <th>Developer</th>
                            <th>Genre</th>
                            <th>Price</th>
                                
                                <c:if test="${isAdmin}">
                                   
                                <th>Action</th>

                            </c:if>
                        </tr>
                    </thead>

                    <tbody>
                        
                        <c:forEach var="g" items="${requestScope.games}">
                            
                        <tr>
                            
                            <td>${g.gameID}</td>
                            <td><img src="assets/img/${g.image}" width="150px"></td>
                            <td>${g.gameName}</td>
                            <td>${g.developer}</td>
                            <td>${g.genre}</td>
                            <td>${g.price}$</td>


                            <c:if test="${isAdmin}">
                                <td><a href="MainController?action=delete&id=${g.gameID}&searchTerm=${searchTerm}">
                                        <img src="assets/img/delete.jpg" style="width: 20px; height: 20px"/>
                                    </a>
                                <a href="MainController?action=edit&id=${g.gameID}&searchTerm=${searchTerm}">
                                        <img src="assets/img/edit.jpg" style="width: 30px; height: 30px"/>
                                    </a>
                                </td>

                            </c:if>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>

            </c:if>
            <c:if test="${not isAdmin}">
                You do not have permission to access this content.
            </c:if>
        </c:if>
    </body>
</html>

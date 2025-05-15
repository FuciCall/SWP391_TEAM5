<%@page import="utils.AuthUtils"%>
<%@page import="dto.GameDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Game Form</title>
    </head>
    <body>

        <%-- Check quyền admin bằng Java trực tiếp --%>
        <%
            boolean isAdmin = AuthUtils.isAdmin(session);
        %>

        <% if (isAdmin) { %>

            <%
                GameDTO game = new GameDTO();
                if (request.getAttribute("game") != null) {
                    game = (GameDTO) request.getAttribute("game");
                } else {
                    request.setAttribute("game", game);
                }

                String action = "add";
                Object actionObj = request.getAttribute("action");
                if (actionObj != null && actionObj.toString().equals("update")) {
                    action = "update";
                }
            %>

            <form action="MainController" method="post">
                <input type="hidden" name="action" value="<%= action %>"/>

                Game ID: <input type="text" name="txtGameID" value="${game.gameID}"/><br/>
                <c:if test="${not empty requestScope.gameID_error}">
                    <span style="color: red">${requestScope.gameID_error}</span><br/>
                </c:if>

                Game Name: <input type="text" name="txtGameName" value="${game.gameName}"/><br/>
                <c:if test="${not empty requestScope.gameName_error}">
                    <span style="color: red">${requestScope.gameName_error}</span><br/>
                </c:if>

                Developer: <input type="text" name="txtDeveloper" value="${game.developer}"/><br/>
                <c:if test="${not empty requestScope.developer_error}">
                    <span style="color: red">${requestScope.developer_error}</span><br/>
                </c:if>

                Genre: <input type="text" name="txtGenre" value="${game.genre}"/><br/>
                <c:if test="${not empty requestScope.genre_error}">
                    <span style="color: red">${requestScope.genre_error}</span><br/>
                </c:if>

                Price: <input type="text" name="txtPrice" value="${game.price}"/><br/>
                <c:if test="${not empty requestScope.price_error}">
                    <span style="color: red">${requestScope.price_error}</span><br/>
                </c:if>

                Image: <input type="text" name="txtImage" value="${game.image}"/><br/>
                <c:if test="${not empty requestScope.image_error}">
                    <span style="color: red">${requestScope.image_error}</span><br/>
                </c:if>

                <input type="submit" value="Save"/>
                <input type="reset" value="Reset"/>
            </form>

        <% } else { %>
            <p style="text-align: center">
                <img src="assets/img/403.jpg" alt="403 - Forbidden"/>
            </p>
        <% } %>

    </body>
</html>

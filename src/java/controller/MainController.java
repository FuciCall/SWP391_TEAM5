package controller;

import dao.GameDAO;
import dao.UserDAO;
import dto.GameDTO;
import dto.UserDTO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.AuthUtils;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    private static final String LOGIN = "login.jsp";
    private static final UserDAO udao = new UserDAO();
    private static final GameDAO gameDAO = new GameDAO();

    private String processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = LOGIN;
        String strUserID = request.getParameter("txtUserID");
        String strPassword = request.getParameter("txtPassword");
        if (AuthUtils.isValidLogin(strUserID, strPassword)) {
            UserDTO user = AuthUtils.getUserDTO(strUserID);
            request.getSession().setAttribute("user", user);

            url = processSearch(request, response); // ✅ PHẢI GÁN LẠI
        } else {
            url = LOGIN;
            request.setAttribute("message", "Incorrect UserID or Password");
        }
        return url;
    }

    private String processLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = LOGIN;
        HttpSession session = request.getSession();
        if (AuthUtils.isLoggedIn(session)) {
            request.getSession().invalidate();
            url = LOGIN;
        }
        return url;
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private String processSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = LOGIN;
        HttpSession session = request.getSession();
        if (AuthUtils.isLoggedIn(session)) {
            String searchTerm = request.getParameter("searchTerm");
            if (searchTerm == null) {
                searchTerm = "";
            }
            List<GameDTO> games = gameDAO.search(searchTerm);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("games", games);
            url = "search.jsp";
        }
        return url;
    }

    private String processDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = LOGIN;
        HttpSession session = request.getSession();
        if (AuthUtils.isAdmin(session)) {
            String id = request.getParameter("id");
            gameDAO.delete(id);
            processSearch(request, response);
            url = "search.jsp";
        }
        return url;
    }

    private String processAdd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = LOGIN;
        HttpSession session = request.getSession();
        if (AuthUtils.isAdmin(session)) {
            try {
                boolean checkError = false;

                String gameID = request.getParameter("txtGameID");
                String gameName = request.getParameter("txtGameName");
                String developer = request.getParameter("txtDeveloper");
                String genre = request.getParameter("txtGenre");
                double price = Double.parseDouble(request.getParameter("txtPrice"));
                String image = request.getParameter("txtImage");

                if (gameID == null || gameID.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("gameID_error", "Game ID cannot be empty.");
                }

                if (gameName == null || gameName.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("gameName_error", "Game Name cannot be empty.");
                }

                if (developer == null || developer.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("developer_error", "Developer cannot be empty.");
                }

                if (genre == null || genre.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("genre_error", "Genre cannot be empty.");
                }

                if (price < 0) {
                    checkError = true;
                    request.setAttribute("price_error", "Price cannot be negative.");
                }

                if (image == null || image.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("image_error", "Image cannot be empty.");
                }

                GameDTO game = new GameDTO(gameID, gameName, developer, genre, price, true, image);
                if (!checkError) {
                    gameDAO.create(game);
                    processSearch(request, response);
                    url = processSearch(request, response);
                } else {
                    request.setAttribute("game", game);
                    url = "gameForm.jsp";
                }
            } catch (Exception e) {
            }
        }
        session.setAttribute("isAdmin", AuthUtils.isAdmin(session));
        return url;
    }

    private String processEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = LOGIN;
        HttpSession session = request.getSession();
        if (AuthUtils.isAdmin(session)) {
            String id = request.getParameter("id");
            GameDTO game = gameDAO.readById(id);
            if (game != null) {
                request.setAttribute("action", "update");
                request.setAttribute("game", game);
                url = "gameForm.jsp";

            } else {
                // search
                url = "search.jsp";
                processSearch(request, response);
            }

        }
        session.setAttribute("isAdmin", AuthUtils.isAdmin(session));
        return url;
    }

    private String processUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = LOGIN;
        HttpSession session = request.getSession();
        if (AuthUtils.isAdmin(session)) {
            try {
                boolean checkError = false;

                String gameID = request.getParameter("txtGameID");
                String gameName = request.getParameter("txtGameName");
                String developer = request.getParameter("txtDeveloper");
                String genre = request.getParameter("txtGenre");
                double price = Double.parseDouble(request.getParameter("txtPrice"));
                String image = request.getParameter("txtImage");

                if (gameID == null || gameID.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("gameID_error", "Game ID cannot be empty.");
                }

                if (gameName == null || gameName.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("gameName_error", "Game Name cannot be empty.");
                }

                if (developer == null || developer.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("developer_error", "Developer cannot be empty.");
                }

                if (genre == null || genre.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("genre_error", "Genre cannot be empty.");
                }

                if (price < 0) {
                    checkError = true;
                    request.setAttribute("price_error", "Price cannot be negative.");
                }

                if (image == null || image.trim().isEmpty()) {
                    checkError = true;
                    request.setAttribute("image_error", "Image cannot be empty.");
                }

                GameDTO game = new GameDTO(gameID, gameName, developer, genre, price, true, image);
                if (!checkError) {
                    gameDAO.update(game);
                    processSearch(request, response);
                    url = processSearch(request, response);
                } else {
                    request.setAttribute("action", "update");
                    request.setAttribute("game", game);
                    url = "gameForm.jsp";
                }
            } catch (Exception e) {
            }
        }
        return url;
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String url = LOGIN;
        try {
            String action = request.getParameter("action");
            if (action == null) {
                url = LOGIN;
            } else {
                if (action.equals("login")) {
                    url = processLogin(request, response);
                } else if (action.equals("logout")) {
                    url = processLogout(request, response);
                } else if (action.equals("search")) {
                    url = processSearch(request, response);
                } else if (action.equals("delete")) {
                    url = processDelete(request, response);
                } else if (action.equals("add")) {
                    url = processAdd(request, response);
                } else if (action.equals("edit")) {
                    url = processEdit(request, response);
                } else if (action.equals("update")) {
                    url = processUpdate(request, response);
                }
            }
        } catch (Exception e) {
            log("Error at MainController: " + e.toString());
        } finally {
            RequestDispatcher rd = request.getRequestDispatcher(url);
            rd.forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

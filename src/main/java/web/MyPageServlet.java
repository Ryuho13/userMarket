package web;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;

import dao.UserDAO;
import model.User;
import model.UserProfile;
import model.ChatDAO;
import model.ChatRoomDisplayDTO;
import model.DBConnection;

@WebServlet("/user/myPage")
public class MyPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login.jsp");
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");

        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO();
            UserProfile profile = userDAO.findProfileByUserId(loginUser.getId());
            
            ChatDAO chatDAO = new ChatDAO(conn);
            List<ChatRoomDisplayDTO> chatRooms = chatDAO.getChatRoomsByUserId(loginUser.getId());

            req.setAttribute("profile", profile);
            req.setAttribute("chatRooms", chatRooms);

            RequestDispatcher rd = req.getRequestDispatcher("/user/myPage.jsp");
            rd.forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "프로필 로드 실패: " + e.getMessage());
        }
    }
}
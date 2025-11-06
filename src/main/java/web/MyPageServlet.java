package web;

import java.io.IOException;
import java.sql.SQLException;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ChatRoomDisplayDTO;

@WebServlet("/user/myPage")
public class MyPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
    	
    	HttpSession session = req.getSession(false);
    	
        if (session == null || session.getAttribute("loginUserId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) session.getAttribute("loginUserId");

        UserDAO dao = new UserDAO();
        try {
            var profile = dao.findProfileByUserId(userId);
            req.setAttribute("user", profile);

            // --- 채팅방 목록 조회 로직 추가 ---
            try (java.sql.Connection conn = model.DBConnection.getConnection()) {
                if (conn != null) {
                    model.ChatDAO chatDAO = new model.ChatDAO(conn);
                    java.util.List<ChatRoomDisplayDTO> chatRooms = chatDAO.getChatRoomsByUserId(userId);
                    req.setAttribute("chatRooms", chatRooms);
                }
            } catch (SQLException e) {
                // 로깅을 추가하거나 사용자에게 오류 페이지를 보여주는 것이 좋습니다.
                e.printStackTrace(); 
            }
            // ---------------------------------

            req.getRequestDispatcher("/user/myPage.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}


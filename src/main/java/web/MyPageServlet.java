package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;

import dao.UserDAO;
import model.User;
import model.UserProfile;
import model.ChatRoomDisplayDTO;

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

        try {
            UserDAO dao = new UserDAO();
            UserProfile profile = dao.findProfileByUserId(loginUser.getId());

            // request 범위로 전달 (JSP에서 requestScope.profile 로 접근)
            req.setAttribute("profile", profile);

            RequestDispatcher rd = req.getRequestDispatcher("/user/myPage.jsp");
            rd.forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "프로필 로드 실패: " + e.getMessage());
        }
    }
}

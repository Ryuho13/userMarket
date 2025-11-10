package web;

import java.io.IOException;
import java.sql.SQLException;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/user/delete")
public class DeleteUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login.jsp");
            return;
        }

        int userId = ((model.User) session.getAttribute("loginUser")).getId();

        try {
            boolean success = userDAO.deleteUserById(userId);

            session.invalidate();

            if (success) {
                // 탈퇴 완료되면 홈으로 이동
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            } else {
                resp.sendError(500, "회원 삭제 실패: DB 반영 안 됨");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(500, "회원 삭제 중 오류: " + e.getMessage());
        }
    }
}

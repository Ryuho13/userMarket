package web;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.UserDAO;
import model.User;

@WebServlet("/user/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ✅ redirect 파라미터 유지 (로그인 후 돌아가기 위함)
        String redirect = req.getParameter("redirect");
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }

        req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String accountId = trimOrNull(req.getParameter("id"));
        String pw = trimOrNull(req.getParameter("pw"));
        String redirect = trimOrNull(req.getParameter("redirect")); // ✅ 추가

        if (accountId == null || pw == null) {
            req.setAttribute("error", "아이디와 비밀번호를 입력하세요.");
            req.setAttribute("lastId", accountId);
            req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
            return;
        }

        UserDAO dao = new UserDAO();

        try {
            User user = dao.login(accountId, pw);

            if (user == null) {
                req.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                req.setAttribute("lastId", accountId);
                req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            try {
                req.changeSessionId();
            } catch (IllegalStateException ignore) {}

            session.setAttribute("loginUserId", user.getId());
            session.setAttribute("loginAccountId", user.getAccountId());

            // ✅ 로그인 성공 후 redirect 있으면 해당 페이지로 이동
            if (redirect != null && redirect.startsWith("/")) {
                resp.sendRedirect(req.getContextPath() + redirect);
            } else {
                resp.sendRedirect(req.getContextPath() + "/user/myPage");
            }

        } catch (SQLException e) {
            throw new ServletException("로그인 처리 중 DB 오류", e);
        }
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

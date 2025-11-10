package web;

import java.io.IOException;
import java.sql.SQLException;

import dao.UserDAO;
import model.User;
import model.UserProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/user/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String id = trim(req.getParameter("id"));
        String pw = trim(req.getParameter("pw"));

        if (isBlank(id) || isBlank(pw)) {
            req.setAttribute("error", "아이디와 비밀번호를 입력하세요.");
            req.setAttribute("id", id);
            req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDAO.login(id, pw);
            if (user == null) {
                req.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                req.setAttribute("id", id);
                req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
                return;
            }

            UserProfile profile = userDAO.findProfileByUserId(user.getId());

            HttpSession session = req.getSession(true);
            session.setAttribute("loginUser", user);
            session.setAttribute("loginProfile", profile);
            session.setAttribute("loginUserId", user.getId());
            // PRG: 상품 목록 서블릿으로 이동 (/product로 경로 변경)
            resp.sendRedirect(req.getContextPath() + "/product"); 
            // 만약 MyPageServlet이 있다면: resp.sendRedirect(req.getContextPath()+"/user/mypage");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            req.setAttribute("id", id);
            req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
        }
    }

    private static String trim(String s){ return s==null? null : s.trim(); }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
}
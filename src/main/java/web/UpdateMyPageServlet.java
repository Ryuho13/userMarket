package web;

import java.io.IOException;
import java.sql.SQLException;

import dao.UserDAO;
import model.User;
import model.UserProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;

@WebServlet("/user/mypage/update")
public class UpdateMyPageServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

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
            UserProfile profile = userDAO.findProfileByUserId(loginUser.getId());
            req.setAttribute("profile", profile);

            // 이메일 화면 채우기 위해 mail1/mail2 분리
            String mail1 = null, mail2 = null;
            if (profile != null && profile.getEm() != null) {
                String em = profile.getEm();
                int at = em.indexOf('@');
                if (at > 0) {
                    mail1 = em.substring(0, at);
                    mail2 = em.substring(at + 1);
                }
            }
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", mail2);

            RequestDispatcher rd = req.getRequestDispatcher("/user/updateMyPage.jsp");
            rd.forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(500, "프로필 로드 실패: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login.jsp");
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");
        int userId = loginUser.getId();

        String name     = trim(req.getParameter("name"));
        String nickname = trim(req.getParameter("nickname"));
        String pw       = trim(req.getParameter("password"));            // 비우면 유지
        String pw2      = trim(req.getParameter("password_confirm"));
        String mail1    = trim(req.getParameter("mail1"));
        String mail2    = trim(req.getParameter("mail2"));
        String phn      = trim(req.getParameter("phone"));
        String addrDet  = trim(req.getParameter("address"));
        String em       = (!isBlank(mail1) && !isBlank(mail2)) ? (mail1 + "@" + mail2) : null;

        if (isBlank(name) || isBlank(nickname)) {
            req.setAttribute("error", "성명과 닉네임은 필수입니다.");
            try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", mail2);
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
            return;
        }
        if ((pw != null || pw2 != null) && !equalsOrEmpty(pw, pw2)) {
            req.setAttribute("error", "비밀번호와 확인이 일치하지 않습니다.");
            try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", mail2);
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
            return;
        }

        String newPwNullable = isBlank(pw) ? null : pw;

        try {
            userDAO.updateUserAndInfo(
                    userId,
                    name,
                    phn,
                    em,             // null이면 이메일 NULL 처리 (원하면 기존 유지 로직으로 바꿔도 됨)
                    newPwNullable,  // null이면 비밀번호 유지
                    nickname,
                    addrDet,
                    null            // profile_img (이미지 업로드는 추후)
            );

            // 세션 표시값도 갱신해 두면 UX 좋음
            loginUser.setName(name);
            loginUser.setPhn(phn);
            loginUser.setEm(em);

            resp.sendRedirect(req.getContextPath() + "/user/myPage");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "회원정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", mail2);
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
        }
    }

    private static String trim(String s){ return s==null? null : s.trim(); }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
    private static boolean equalsOrEmpty(String a, String b){
        if (isBlank(a) && isBlank(b)) return true;
        return a != null && a.equals(b);
    }
}

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

        // --- 이메일: mail3-input(직접입력)이 있으면 그것을 도메인으로 사용 ---
        String mail1    = trim(req.getParameter("mail1"));
        String mail2Sel = trim(req.getParameter("mail2"));               // select 값 (직접입력 선택 시 빈 문자열일 수 있음)
        String mail2Dir = trim(req.getParameter("mail3-input"));         // 직접입력 input (존재할 수도)
        String domain   = !isBlank(mail2Dir) ? mail2Dir : mail2Sel;      // 우선순위: 직접입력 > 선택값
        String em       = (!isBlank(mail1) && !isBlank(domain)) ? (mail1 + "@" + domain) : null;

        // --- 전화번호 ---
        String phn      = trim(req.getParameter("phone"));

        // --- 주소: addr1/addr2/addr3를 공백으로 합쳐 addrDet로 저장 ---
        String addr1 = trim(req.getParameter("addr1"));
        String addr2 = trim(req.getParameter("addr2")); // 주의: 클라이언트에서 disabled면 아예 안 넘어올 수 있음
        String addr3 = trim(req.getParameter("addr3"));

        String addrDet = null;
        if (!isBlank(addr1) || !isBlank(addr2) || !isBlank(addr3)) {
            StringBuilder sb = new StringBuilder();
            if (!isBlank(addr1)) sb.append(addr1);
            if (!isBlank(addr2)) { if (sb.length() > 0) sb.append(' '); sb.append(addr2); }
            if (!isBlank(addr3)) { if (sb.length() > 0) sb.append(' '); sb.append(addr3); }
            addrDet = sb.toString();
        }

        // --- 기본 검증 ---
        if (isBlank(name) || isBlank(nickname)) {
            req.setAttribute("error", "성명과 닉네임은 필수입니다.");
            try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", domain);
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
            return;
        }
        if ((pw != null || pw2 != null) && !equalsOrEmpty(pw, pw2)) {
            req.setAttribute("error", "비밀번호와 확인이 일치하지 않습니다.");
            try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", domain);
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
            return;
        }

        String newPwNullable = isBlank(pw) ? null : pw;

        try {
            userDAO.updateUserAndInfo(
                    userId,
                    name,
                    phn,
                    em,             // null이면 이메일 NULL 처리 (원하면 기존 유지 정책으로 바꿔도 됨)
                    newPwNullable,  // null이면 비밀번호 유지
                    nickname,
                    addrDet,        // ✅ 합쳐진 주소 문자열
                    null            // profile_img (추후)
            );

            // 세션 표시값도 갱신
            loginUser.setName(name);
            loginUser.setPhn(phn);
            loginUser.setEm(em);

            resp.sendRedirect(req.getContextPath() + "/user/myPage");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "회원정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", domain);
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

package web;

import java.io.IOException;
import java.sql.SQLException;

import dao.UserDAO;
import model.User;
import model.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/user/add")
public class AddUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/user/addUser.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String id        = trim(req.getParameter("id"));
        String pw        = trim(req.getParameter("pw"));
        String name      = trim(req.getParameter("name"));
        String nickname  = trim(req.getParameter("nickname"));
        String em        = trim(req.getParameter("em"));
        String phn       = trim(req.getParameter("phn"));
        String regionStr = trim(req.getParameter("region_id"));
        String addrDet   = trim(req.getParameter("addr_detail"));

        // 기본 검증
        if (isBlank(id) || isBlank(pw)) {
            req.setAttribute("error", "아이디/비밀번호는 필수입니다.");
            keep(req, id, name, nickname, em, phn, regionStr, addrDet);
            req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
            return;
        }

        Integer regionId = null;
        try {
            if (!isBlank(regionStr)) regionId = Integer.valueOf(regionStr);
        } catch (NumberFormatException ignore) {}

        // DTO 구성
        User u = new User();
        u.setAccountId(id);
        u.setPw(pw);
        u.setName(name);
        u.setEm(em);
        u.setPhn(phn);

        UserInfo ui = new UserInfo();
        ui.setNickname(nickname);
        ui.setRegionId(regionId);
        ui.setAddrDetail(addrDet);
        ui.setProfileImg(null); // 이미지 업로드 전이라 null

        try {
            int newUserId = userDAO.createUserWithInfo(u, ui);

            // 새로고침 중복 방지 & 성공 메시지 표시
            resp.sendRedirect(req.getContextPath() + "/user/welcome.jsp?uid=" + newUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            keep(req, id, name, nickname, em, phn, regionStr, addrDet);
            req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
        }
    }

    private static String trim(String s){ return s==null? null : s.trim(); }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
    private static void keep(HttpServletRequest req, String id, String name, String nickname,
                             String em, String phn, String regionIdStr, String addrDetail) {
        req.setAttribute("id", id);
        req.setAttribute("name", name);
        req.setAttribute("nickname", nickname);
        req.setAttribute("em", em);
        req.setAttribute("phn", phn);
        req.setAttribute("region_id", regionIdStr);
        req.setAttribute("addr_detail", addrDetail);
    }
}

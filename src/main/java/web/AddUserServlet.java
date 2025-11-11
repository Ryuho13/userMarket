package web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.AreaDAO;
import dao.UserDAO;
import model.SidoArea;
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
    private final AreaDAO areaDAO = new AreaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<SidoArea> list = areaDAO.getAllSidos();
            req.setAttribute("sidoList", list);
        } catch (Exception e) {
            throw new ServletException(e);
        }
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
        String mail1     = trim(req.getParameter("mail1"));
        String mail2     = trim(req.getParameter("mail2"));
        String em        = (!isBlank(mail1) && !isBlank(mail2)) ? (mail1 + "@" + mail2) : null;
        String phn       = trim(req.getParameter("phn"));

        // 새 폼 이름
        String sidoIdStr = trim(req.getParameter("sidoId"));     // 선택 시/도 (선택값 유지용)
        String regionStr = trim(req.getParameter("regionId"));   // 저장할 시/군/구 PK
        if (isBlank(regionStr)) {
            regionStr = trim(req.getParameter("siggId"));        // 예전/다른 이름 fallback
        }
        String addr3     = trim(req.getParameter("addr3"));      // 상세 주소

        // 기본 검증
        if (isBlank(id) || isBlank(pw)) {
            req.setAttribute("error", "아이디/비밀번호는 필수입니다.");
            keep(req, id, name, nickname, em, phn, sidoIdStr, regionStr, addr3);
            reloadAreasAndForward(req, resp, sidoIdStr);
            return;
        }

        // regionId 파싱
        Integer regionId = null;
        try {
            if (!isBlank(regionStr)) regionId = Integer.valueOf(regionStr);
        } catch (NumberFormatException ignore) {}

        // ✅ addr_detail = "시/도명 시/군/구명 상세주소" 로 합치기 (이름은 DB에서 조회)
        String addrDet = addr3; // 기본 fallback
        try {
            String sidoName = null;
            if (!isBlank(sidoIdStr)) {
                try { 
                    int sid = Integer.parseInt(sidoIdStr);
                    sidoName = areaDAO.getSidoNameById(sid);
                } catch (NumberFormatException ignore) {}
            }

            String siggName = null;
            if (regionId != null) {
                siggName = areaDAO.getSiggNameById(regionId);
            }

            StringBuilder sb = new StringBuilder();
            if (!isBlank(sidoName)) sb.append(sidoName).append(' ');
            if (!isBlank(siggName)) sb.append(siggName).append(' ');
            if (!isBlank(addr3))    sb.append(addr3);

            String merged = sb.toString().trim();
            if (!merged.isEmpty()) addrDet = merged;

        } catch (Exception ignore) {
            // 이름 조회 실패 시 addr3만 저장 (fallback)
        }

        // DTO 구성
        User u = new User();
        u.setAccountId(id);
        u.setPw(pw);
        u.setName(name);
        u.setEm(em);
        u.setPhn(phn);

        UserInfo ui = new UserInfo();
        ui.setNickname(nickname);
        ui.setRegionId(regionId);     // ← 시/군/구 FK
        ui.setAddrDetail(addrDet);    // ← "시/도 시/군/구 상세주소"
        ui.setProfileImg(null);

        try {
            int newUserId = userDAO.createUserWithInfo(u, ui);
            resp.sendRedirect(req.getContextPath() + "/user/welcome.jsp?uid=" + newUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            keep(req, id, name, nickname, em, phn, sidoIdStr, regionStr, addr3);
            reloadAreasAndForward(req, resp, sidoIdStr);
        }
    }

    private void reloadAreasAndForward(HttpServletRequest req, HttpServletResponse resp, String sidoIdStr)
            throws ServletException, IOException {
        try {
            req.setAttribute("sidoList", areaDAO.getAllSidos());
            if (!isBlank(sidoIdStr)) {
                try {
                    int sid = Integer.parseInt(sidoIdStr);
                    req.setAttribute("siggList", areaDAO.getSiggsBySido(sid));
                } catch (NumberFormatException ignore) {}
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
    }

    private static String trim(String s){ return s == null ? null : s.trim(); }
    private static boolean isBlank(String s){ return s == null || s.isBlank(); }

    private static void keep(HttpServletRequest req, String id, String name, String nickname,
                             String em, String phn, String sidoIdStr, String regionIdStr, String addrDetail) {
        req.setAttribute("id", id);
        req.setAttribute("name", name);
        req.setAttribute("nickname", nickname);
        req.setAttribute("em", em);
        req.setAttribute("phn", phn);
        req.setAttribute("sidoId", sidoIdStr);
        req.setAttribute("regionId", regionIdStr);
        req.setAttribute("addr3", addrDetail);
    }
}

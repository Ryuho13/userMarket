package web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import dao.AreaDAO;
import dao.UserDAO;
import model.SidoArea;
import model.SiggArea;
import model.User;
import model.UserProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Part;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,   // 1MB
    maxFileSize = 1024 * 1024 * 5,     // 5MB
    maxRequestSize = 1024 * 1024 * 10  // 10MB
)
@WebServlet("/user/mypage/update")
public class UpdateMyPageServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AreaDAO areaDAO = new AreaDAO();

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

            // 이메일 분리
            String mail1 = null, mail2 = null;
            if (profile != null && profile.getEm() != null) {
                String em = profile.getEm();
                int at = em.indexOf('@');
                if (at > 0) { mail1 = em.substring(0, at); mail2 = em.substring(at + 1); }
            }
            req.setAttribute("mail1", mail1);
            req.setAttribute("mail2", mail2);

            // 주소 선택 목록
            List<SidoArea> sidos = areaDAO.getAllSidos();
            req.setAttribute("sidoList", sidos);

            Integer regionId = (profile != null) ? profile.getRegionId() : null;
            Integer sidoId = null;
            if (regionId != null) {
                sidoId = areaDAO.getSidoIdBySiggId(regionId);
                req.setAttribute("regionId", regionId);
                req.setAttribute("sidoId", sidoId);

                if (sidoId != null) {
                    List<SiggArea> siggs = areaDAO.getSiggsBySido(sidoId);
                    req.setAttribute("siggList", siggs);
                }
            }

            // addr_detail -> addr3 분리 (시/도 + 시/군/구 접두어 제거)
            String addr3 = null;
            if (profile != null && profile.getAddrDetail() != null && !profile.getAddrDetail().isBlank()) {
                String det = profile.getAddrDetail().trim();

                if (sidoId != null && regionId != null) {
                    String sidoName = areaDAO.getSidoNameById(sidoId);
                    String siggName = areaDAO.getSiggNameById(regionId);
                    if (sidoName != null && siggName != null) {
                        String prefix = (sidoName + " " + siggName).trim();
                        if (det.startsWith(prefix)) {
                            addr3 = det.substring(prefix.length()).trim();
                        }
                    }
                }
                // 안전망: 위에서 못 잘랐으면 3분할 시도
                if (addr3 == null || addr3.isBlank()) {
                    String[] parts = det.split("\\s+", 3);
                    if (parts.length == 3) addr3 = parts[2];
                    else addr3 = det; // 그래도 못 자르면 전체 유지
                }
            }
            req.setAttribute("addr3", addr3);

            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "프로필 로드 실패: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login.jsp");
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");
        int userId = loginUser.getId();

        // ---------- 파일 업로드 ----------
        String profileImgPath = null;
        Part filePart = null;
        try {
            filePart = req.getPart("profile-upload");
        } catch (Exception ignore) {}

        if (filePart != null && filePart.getSize() > 0 &&
            filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
            String uploadDir = "/profile_images";
            String savePath = getServletContext().getRealPath(uploadDir);

            File saveDir = new File(savePath);
            if (!saveDir.exists()) saveDir.mkdirs();

            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String ext = "";
            int dot = fileName.lastIndexOf('.');
            if (dot >= 0) ext = fileName.substring(dot);

            String uniqueFileName = "user_" + userId + "_" + System.currentTimeMillis() + ext;

            try {
                filePart.write(savePath + File.separator + uniqueFileName);
                profileImgPath = uploadDir + "/" + uniqueFileName;
            } catch (IOException e) {
                e.printStackTrace();
                req.setAttribute("error", "프로필 이미지 저장 중 오류가 발생했습니다.");
                try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
                req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
                return;
            }
        }

        // ---------- 폼 파라미터 ----------
        String name     = trim(req.getParameter("name"));
        String nickname = trim(req.getParameter("nickname"));
        String pw       = trim(req.getParameter("password"));
        String pw2      = trim(req.getParameter("password_confirm"));

        String mail1    = trim(req.getParameter("mail1"));
        String mail2Sel = trim(req.getParameter("mail2"));
        String mail2Dir = trim(req.getParameter("mail3-input"));
        String domain   = !isBlank(mail2Dir) ? mail2Dir : mail2Sel;
        String em       = (!isBlank(mail1) && !isBlank(domain)) ? (mail1 + "@" + domain) : null;

        String phn      = trim(req.getParameter("phone"));

        // 새 주소 이름: sidoId / regionId / addr3
        String sidoIdStr = trim(req.getParameter("sidoId"));
        String regionStr = trim(req.getParameter("regionId"));
        String addr3     = trim(req.getParameter("addr3"));

        // ---------- 검증 ----------
        if (isBlank(name) || isBlank(nickname)) {
            setReloadAttributesOnError(req, userId, mail1, domain);
            req.setAttribute("error", "성명과 닉네임은 필수입니다.");
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
            return;
        }
        if ((pw != null || pw2 != null) && !equalsOrEmpty(pw, pw2)) {
            setReloadAttributesOnError(req, userId, mail1, domain);
            req.setAttribute("error", "비밀번호와 확인이 일치하지 않습니다.");
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
            return;
        }

        Integer regionIdNullable = null;
        try {
            if (!isBlank(regionStr)) regionIdNullable = Integer.valueOf(regionStr);
        } catch (NumberFormatException ignore) {}

        // ---------- 주소 병합: "시/도 시/군/구 상세주소" ----------
        String addrDetail = addr3; // 최후 fallback
        try {
            String sidoName = null, siggName = null;
            if (!isBlank(sidoIdStr)) {
                try { sidoName = areaDAO.getSidoNameById(Integer.parseInt(sidoIdStr)); } catch (NumberFormatException ignore) {}
            }
            if (regionIdNullable != null) {
                siggName = areaDAO.getSiggNameById(regionIdNullable);
            }

            StringBuilder sb = new StringBuilder();
            if (!isBlank(sidoName)) sb.append(sidoName).append(' ');
            if (!isBlank(siggName)) sb.append(siggName).append(' ');
            if (!isBlank(addr3))    sb.append(addr3);
            String merged = sb.toString().trim();
            if (!merged.isEmpty()) addrDetail = merged;

        } catch (Exception ignore) { /* addr3만 저장 */ }

        String newPwNullable = isBlank(pw) ? null : pw;

        // ---------- DB 업데이트 ----------
        try {
            // ⚠️ UserDAO.updateUserAndInfo 시그니처는 다음과 같아야 함:
            // updateUserAndInfo(int userId, String name, String phn, String emNullable,
            //                   String newPwNullable, String nickname, String addrDetail,
            //                   String profileImgNullable, Integer regionIdNullable)
            userDAO.updateUserAndInfo(
                    userId,
                    name,
                    phn,
                    em,
                    newPwNullable,
                    nickname,
                    addrDetail,
                    profileImgPath,   // 새 이미지 없으면 null → 기존 유지
                    regionIdNullable  // ✅ region_id 업데이트
            );

            // 세션 표시값 갱신
            loginUser.setName(name);
            loginUser.setPhn(phn);
            loginUser.setEm(em);

            resp.sendRedirect(req.getContextPath() + "/user/myPage");
        } catch (SQLException e) {
            e.printStackTrace();
            setReloadAttributesOnError(req, userId, mail1, domain);
            req.setAttribute("error", "회원정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
        }
    }

    private void setReloadAttributesOnError(HttpServletRequest req, int userId, String mail1, String domain) {
        try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
        req.setAttribute("mail1", mail1);
        req.setAttribute("mail2", domain);

        try {
            req.setAttribute("sidoList", areaDAO.getAllSidos());
            UserProfile p = (UserProfile) req.getAttribute("profile");
            if (p != null && p.getRegionId() != null) {
                Integer sid = areaDAO.getSidoIdBySiggId(p.getRegionId());
                if (sid != null) {
                    req.setAttribute("sidoId", sid);
                    req.setAttribute("siggList", areaDAO.getSiggsBySido(sid));
                    req.setAttribute("regionId", p.getRegionId());
                }
            }
        } catch (Exception ignored) {}
    }

    private static String trim(String s){ return s==null? null : s.trim(); }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
    private static boolean equalsOrEmpty(String a, String b){
        if (isBlank(a) && isBlank(b)) return true;
        return a != null && a.equals(b);
    }
}

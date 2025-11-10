package web;

import java.io.File; // File 처리를 위해 추가
import java.io.IOException;
import java.nio.file.Paths; // 파일명 처리를 위해 추가
import java.sql.SQLException;

import dao.UserDAO;
import model.User;
import model.UserProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig; // ✅ 파일 업로드 설정을 위해 추가
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Part; // ✅ 파일 데이터를 처리하기 위해 추가

// 파일 업로드 설정 추가 (최대 크기 등)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 5,   // 5MB
    maxRequestSize = 1024 * 1024 * 10 // 10MB
)
@WebServlet("/user/mypage/update")
public class UpdateMyPageServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // ... (기존 doGet 로직 유지) ...
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

        // Multipart 요청에서는 이 인코딩 설정이 일반 파라미터 로드에 직접 영향을 주지 않을 수 있습니다.
        // req.setCharacterEncoding("UTF-8"); 
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login.jsp");
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");
        int userId = loginUser.getId();

        // 1. ==================== 파일 업로드 처리 로직 ====================
        String profileImgPath = null;
        Part filePart = null;
        
        try {
            // JSP에서 설정한 name="profile-upload"로 Part를 가져옵니다.
            filePart = req.getPart("profile-upload"); 
        } catch (Exception e) {
            // 파일 파트가 없거나 오류 발생 시 무시하고 진행 (null로 유지)
        }

        // 파일이 존재하고, 사이즈가 0보다 크며, 파일명이 있는 경우에만 처리
        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
            
            // 1) 파일 저장 경로 설정 (웹에서 접근 가능한 폴더)
            String uploadDir = "/profile_images"; // DB에 저장할 웹 경로
            String savePath = getServletContext().getRealPath(uploadDir); // 실제 서버 물리적 경로
            
            // 2) 저장 폴더 생성 (없을 경우)
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // 3) 고유한 파일 이름 생성 (중복 방지: user ID와 시간 조합)
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String ext = "";
            if (fileName.contains(".")) {
                 ext = fileName.substring(fileName.lastIndexOf("."));
            }
            String uniqueFileName = "user_" + userId + "_" + System.currentTimeMillis() + ext;
            
            // 4) 파일 저장 및 DB 경로 설정
            try {
                filePart.write(savePath + File.separator + uniqueFileName); // 파일 저장
                profileImgPath = uploadDir + "/" + uniqueFileName; // DB에 저장할 웹 경로 설정
            } catch (IOException e) {
                e.printStackTrace();
                // 파일 저장 실패 시 에러 처리 후 반환
                req.setAttribute("error", "프로필 이미지 저장 중 오류가 발생했습니다.");
                try { req.setAttribute("profile", userDAO.findProfileByUserId(userId)); } catch (SQLException ignored) {}
                // 필요한 경우 mail1/mail2/domain 설정 로직 추가 필요
                req.getRequestDispatcher("/user/updateMyPage.jsp").forward(req, resp);
                return;
            }
        }
        // =============================================================
        
        // 2. ==================== 일반 파라미터 처리 (기존 로직 유지) ====================
        
        // 파일 업로드 시에는 파라미터 추출 전에 인코딩 설정을 다시 확인하는 것이 안전합니다.
        // 하지만 getParameter()는 Part 처리 후에도 대부분의 컨테이너에서 정상 작동합니다.
        
        String name     = trim(req.getParameter("name"));
        String nickname = trim(req.getParameter("nickname"));
        String pw       = trim(req.getParameter("password"));            // 비우면 유지
        String pw2      = trim(req.getParameter("password_confirm"));

        // --- 이메일 처리 ---
        String mail1    = trim(req.getParameter("mail1"));
        String mail2Sel = trim(req.getParameter("mail2"));               
        String mail2Dir = trim(req.getParameter("mail3-input"));         
        String domain   = !isBlank(mail2Dir) ? mail2Dir : mail2Sel;      
        String em       = (!isBlank(mail1) && !isBlank(domain)) ? (mail1 + "@" + domain) : null;

        // --- 전화번호 ---
        String phn      = trim(req.getParameter("phone"));

        // --- 주소 처리 ---
        String addr1 = trim(req.getParameter("addr1"));
        String addr2 = trim(req.getParameter("addr2")); 
        String addr3 = trim(req.getParameter("addr3"));

        String addrDet = null;
        if (!isBlank(addr1) || !isBlank(addr2) || !isBlank(addr3)) {
            StringBuilder sb = new StringBuilder();
            if (!isBlank(addr1)) sb.append(addr1);
            if (!isBlank(addr2)) { if (sb.length() > 0) sb.append(' '); sb.append(addr2); }
            if (!isBlank(addr3)) { if (sb.length() > 0) sb.append(' '); sb.append(addr3); }
            addrDet = sb.toString();
        }

        // 3. ==================== 유효성 검증 (기존 로직 유지) ====================
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

        // 4. ==================== DB 업데이트 (파일 경로 포함) ====================
        try {
            userDAO.updateUserAndInfo(
                    userId,
                    name,
                    phn,
                    em,             // 이메일
                    newPwNullable,  // 비밀번호 (null이면 유지)
                    nickname,
                    addrDet,        // 합쳐진 주소 문자열
                    profileImgPath  // ✅ 파일 업로드 성공 시 경로, 실패 시 null (기존 유지)
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
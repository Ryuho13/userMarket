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
        
        // ✅ Redirect URL 파라미터를 미리 추출
        String redirectUrl = req.getParameter("redirect"); 

        if (isBlank(id) || isBlank(pw)) {
            req.setAttribute("error", "아이디와 비밀번호를 입력하세요.");
            req.setAttribute("id", id);
            // 리다이렉트 파라미터가 있었다면 에러 페이지에 다시 전달 (login.jsp가 받음)
            if (!isBlank(redirectUrl)) {
                req.setAttribute("redirect", redirectUrl);
            }
            req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDAO.login(id, pw);
            if (user == null) {
                req.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                req.setAttribute("id", id);
                // 리다이렉트 파라미터가 있었다면 에러 페이지에 다시 전달
                if (!isBlank(redirectUrl)) {
                    req.setAttribute("redirect", redirectUrl);
                }
                req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
                return;
            }

            UserProfile profile = userDAO.findProfileByUserId(user.getId());

            HttpSession session = req.getSession(true);
            session.setAttribute("loginUser", user);
            session.setAttribute("loginProfile", profile);
            session.setAttribute("loginUserId", user.getId());
            
            // =================================================================
            // ✅ 최종 리다이렉트 로직: redirect 파라미터 처리
            
            if (!isBlank(redirectUrl)) {
                // 1. redirectUrl에서 상품 ID 값만 추출합니다.
                String productId = "";
                if (redirectUrl.contains("id=")) {
                    // 'id=' 뒤의 값을 찾거나, 전체 쿼리 문자열을 파싱하는 로직이 필요합니다.
                    // 현재는 간단하게 파싱하여 id를 가져온다고 가정합니다.
                    // (참고: 실제 구현 시 URL 파싱이 더 복잡할 수 있습니다.)
                    try {
                        productId = redirectUrl.substring(redirectUrl.indexOf("id=") + 3);
                        if (productId.contains("&")) { // & 기호 이후 값 제거
                            productId = productId.substring(0, productId.indexOf("&"));
                        }
                    } catch (Exception ignored) {
                        // 파싱 실패 시 ID를 비워둡니다.
                    }
                }
                
                if (!productId.isBlank()) {
                    // 2. JSP 경로 대신 서블릿 경로를 강제하여 주소를 만듭니다.
                    String finalRedirectUrl = req.getContextPath() + "/product/detail?id=" + productId;
                    resp.sendRedirect(finalRedirectUrl);
                } else {
                    // ID가 없으면 기본 상품 목록으로 이동
                    resp.sendRedirect(req.getContextPath() + "/product");
                }
            } else {
                // redirectUrl이 없으면 기본 상품 목록으로 이동
                resp.sendRedirect(req.getContextPath() + "/product"); 
            }
            // =================================================================
            
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
            req.setAttribute("id", id);
            if (!isBlank(redirectUrl)) {
                req.setAttribute("redirect", redirectUrl);
            }
            // ✅ 수정: SQLException 발생 시 로그인 페이지로 안전하게 포워딩
            req.getRequestDispatcher("/user/login.jsp").forward(req, resp);
        }
    }

    private static String trim(String s){ return s==null? null : s.trim(); }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
}
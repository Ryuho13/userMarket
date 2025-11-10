package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/user/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 1. 현재 세션을 가져옵니다. (세션이 없으면 null 반환)
        // 로그인 상태이므로 세션이 있을 것으로 예상되지만, 방어적인 코딩을 위해 false를 사용합니다.
        HttpSession session = req.getSession(false);
        
        // 2. 세션이 존재하는 경우, 세션을 무효화합니다.
        // 세션 무효화는 세션에 저장된 모든 사용자 정보(loginUser 포함)를 삭제합니다.
        if (session != null) {
            session.invalidate();
            // System.out.println("사용자 세션 무효화 완료."); 
        }
        
        // 3. 로그아웃 후 메인 페이지 또는 로그인 페이지로 리다이렉트합니다.
        // 여기서는 메인 페이지(/product)로 리다이렉트합니다.
        resp.sendRedirect(req.getContextPath() + "/product/list"); 
    }
    
    // 로그아웃은 GET 요청으로 처리하는 것이 일반적이므로 doPost는 구현하지 않습니다.
}
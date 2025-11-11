package web;

import dao.ProductDAO;
import dao.UserDAO;
// (중략: 필요한 import 구문들)
import model.Product;
import model.User;
import model.UserProfile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.ChatDAO;
import model.ChatRoomDisplayDTO;
import model.DBConnection;

@WebServlet("/user/myPage")
public class MyPageServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login.jsp"); 
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");
        int userId = loginUser.getId();
        
        String contextPath = req.getContextPath(); 

        try {
            UserProfile profile = userDAO.findProfileByUserId(userId);
            if (profile == null) {
                resp.sendError(500, "사용자 프로필 정보를 찾을 수 없습니다. (ID: " + userId + ")");
                return;
            }
            
            // 3. 등록 상품 목록 조회 및 URL 변환 (이 로직은 웹 URL을 정확히 생성합니다.)
            List<Product> products = productDAO.getProductsBySellerId(userId);
            
            // ✅ 이미지 경로 변환 로직: 웹 브라우저가 ImageServlet으로 요청할 URL을 완성합니다.
            for (Product p : products) {
                String thumb = p.getImgName();
                if (thumb != null && !thumb.isBlank()) {
                    if (!thumb.startsWith("http")) {
                        // ImageServlet을 통해 접근 가능한 경로로 설정
                        p.setImgName(contextPath + "/upload/product_images/" + thumb); 
                    }
                } else {
                    // 이미지가 없을 경우 기본 이미지 경로 설정
                    p.setImgName(contextPath + "/resources/images/noimage.jpg"); 
                }
            }

            // 4. 채팅 목록 조회
            List<ChatRoomDisplayDTO> chatRooms;
            try (java.sql.Connection conn = model.DBConnection.getConnection()) {
                if (conn == null) {
                    throw new SQLException("DB 연결 실패: Chat DAO 인스턴스화 불가"); 
                }
                ChatDAO chatDAO = new ChatDAO(conn);
                chatRooms = chatDAO.getChatRoomsByUserId(userId);
            }

            req.setAttribute("profile", profile);
            req.setAttribute("products", products);
            req.setAttribute("chatRooms", chatRooms);

            RequestDispatcher rd = req.getRequestDispatcher("/user/myPage.jsp");
            rd.forward(req, resp);

        } catch (SQLException e) { 
            e.printStackTrace();
            resp.sendError(500, "데이터베이스 오류: 마이페이지 데이터 로드 중 오류: " + e.getMessage());
        } catch (Exception e) { 
            e.printStackTrace();
            resp.sendError(500, "마이페이지 처리 중 예상치 못한 오류: " + e.getMessage());
        }
    }
}
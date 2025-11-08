package web;

import java.io.IOException;
import java.sql.*;
import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/product/wishlist")
public class WishListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L; // ✅ 추가

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("loginUserId") : null;

        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"로그인이 필요합니다.\"}");
            return;
        }

        String productIdStr = req.getParameter("productId");
        String isWishStr = req.getParameter("isWish");

        if (productIdStr == null || isWishStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"잘못된 요청입니다.\"}");
            return;
        }

        int productId;
        boolean isWish;
        try {
            productId = Integer.parseInt(productIdStr);
            isWish = Boolean.parseBoolean(isWishStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"파라미터 형식이 올바르지 않습니다.\"}");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {

            if (isWish) {
                // ✅ 찜 해제
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM wish_lists WHERE register_id = ? AND product_id = ?")) {
                    ps.setInt(1, userId);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }
            } else {
                // ✅ 찜 등록 (중복 방지)
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT IGNORE INTO wish_lists (register_id, product_id) VALUES (?, ?)")) {
                    ps.setInt(1, userId);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }
            }

            // ✅ 현재 찜 상태 확인
            boolean nowWished = false;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM wish_lists WHERE register_id = ? AND product_id = ?")) {
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    nowWished = rs.next();
                }
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"success\":true,\"isWished\":" + nowWished + "}");

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println(">>> [WishListServlet] userId=" + userId + ", productId=" + productId + ", isWish=" + isWish);

            resp.getWriter().write("{\"error\":\"DB 처리 중 오류가 발생했습니다.\"}");
        }
    }
}

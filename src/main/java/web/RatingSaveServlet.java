package web;

import java.io.IOException;
import java.sql.*;

import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/rating/save")
public class RatingSaveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Integer loginUserId = (session != null)
                ? (Integer) session.getAttribute("loginUserId")
                : null;

        if (loginUserId == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        String productIdParam = req.getParameter("productId");
        String ratingParam    = req.getParameter("rating");
        String comment        = req.getParameter("comment");

        if (productIdParam == null || ratingParam == null ||
            productIdParam.isBlank() || ratingParam.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "필수 데이터가 없습니다.");
            return;
        }

        int productId;
        int rating;
        try {
            productId = Integer.parseInt(productIdParam);
            rating = Integer.parseInt(ratingParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 요청입니다.");
            return;
        }

        if (rating < 1 || rating > 5) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "평점은 1~5점 사이여야 합니다.");
            return;
        }

        int sellerId;

        // 1) 상품에서 seller_id 가져오기
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT seller_id FROM products WHERE id = ?")) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "존재하지 않는 상품입니다.");
                    return;
                }
                sellerId = rs.getInt("seller_id");
            }
        } catch (SQLException e) {
            throw new ServletException("판매자 조회 중 오류", e);
        }

        // (선택) chat_room 테이블을 사용해서 loginUserId가 실제 buyer인지 확인해도 좋음.

        // 2) seller_ratings 에 저장 (이미 있으면 업데이트)
        String sql = """
            INSERT INTO seller_ratings (seller_id, buyer_id, product_id, rating, comment)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE rating = VALUES(rating),
                                    comment = VALUES(comment)
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            ps.setInt(2, loginUserId);
            ps.setInt(3, productId);
            ps.setInt(4, rating);
            ps.setString(5, comment);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ServletException("평점 저장 중 오류", e);
        }

        resp.sendRedirect(req.getContextPath() + "/user/myPage");
    }
}

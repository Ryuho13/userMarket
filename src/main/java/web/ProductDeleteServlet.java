package web;

import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/product/delete")
public class ProductDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUserId") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        int userId = (Integer) session.getAttribute("loginUserId");
        int productId = Integer.parseInt(req.getParameter("id"));
        String uploadPath = "D:/upload/product_images"; 

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // ✅ 1. 본인 상품인지 확인
            if (!ownsProduct(conn, productId, userId)) {
                conn.rollback();
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "삭제 권한이 없습니다.");
                return;
            }

            // ✅ 2. 모든 연관 데이터 삭제
            deleteAllRelatedData(conn, productId, uploadPath);

            // ✅ 3. 상품 삭제
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
                ps.setInt(1, productId);
                ps.executeUpdate();
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 전체 삭제 중 오류 발생", e);
        }

        resp.sendRedirect(req.getContextPath() + "/product/list");
    }

    /** 상품 소유자 검증 */
    private boolean ownsProduct(Connection conn, int productId, int userId) throws SQLException {
        String sql = "SELECT id FROM products WHERE id = ? AND seller_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** ✅ 모든 관련 데이터 삭제 */
    private void deleteAllRelatedData(Connection conn, int productId, String uploadPath) throws SQLException {

        // 💬 채팅방 및 메시지 (chat_room → chat_messages는 CASCADE)
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chat_room WHERE product_id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }

        // 💖 찜 목록
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM wish_lists WHERE product_id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }

        // 🖼️ 이미지 및 매핑 삭제
        deleteProductImages(conn, productId, uploadPath);
    }

    /** ✅ 이미지 파일 및 DB 매핑 삭제 */
    private void deleteProductImages(Connection conn, int productId, String uploadPath) throws SQLException {
        List<String> imageNames = new ArrayList<>();

        // 파일 이름 조회
        String sql = """
            SELECT i.name
            FROM images i
            JOIN product_images pi ON i.id = pi.image_id
            WHERE pi.product_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imageNames.add(rs.getString("name"));
                }
            }
        }

        // 실제 파일 삭제
        for (String name : imageNames) {
            File f = new File(uploadPath, name);
            if (f.exists() && f.delete()) {
                System.out.println("🗑️ 파일 삭제됨: " + f.getAbsolutePath());
            }
        }

        // 매핑 테이블 삭제
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product_images WHERE product_id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }

        // 연결 안 된 이미지 정리
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM images WHERE id NOT IN (SELECT image_id FROM product_images)")) {
            ps.executeUpdate();
        }
    }
}

package web;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/product/insert")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10)
public class ProductInsertServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        Integer loginUserId = (Integer) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        // 📥 파라미터 수집
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String sellPriceStr = req.getParameter("sellPrice");
        String categoryIdStr = req.getParameter("categoryId");
        String siggAreaIdStr = req.getParameter("regionId");  // 시군구
        String sidoAreaIdStr = req.getParameter("sidoId");    // 시도

        int sellPrice = (sellPriceStr != null && !sellPriceStr.isEmpty())
                ? Integer.parseInt(sellPriceStr) : 0;
        int categoryId = Integer.parseInt(categoryIdStr);
        String status = "SALE";

        // 📂 이미지 업로드 경로
        String uploadPath = "D:/upload/product_images";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // 🗺️ 유저 활동 지역 등록 (최초 등록 시만)
            if (siggAreaIdStr != null && !siggAreaIdStr.isEmpty()) {
                int siggAreaId = Integer.parseInt(siggAreaIdStr);

                String checkSql = "SELECT COUNT(*) FROM activity_areas WHERE user_id = ?";
                try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                    psCheck.setInt(1, loginUserId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            String insertSql = """
                                INSERT INTO activity_areas (user_id, sigg_area_id, distance_meters, emd_area_ids)
                                VALUES (?, ?, 2000, '[]')
                            """;
                            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                                psInsert.setInt(1, loginUserId);
                                psInsert.setInt(2, siggAreaId);
                                psInsert.executeUpdate();
                                System.out.println("✅ activity_areas 등록 완료 (user_id=" + loginUserId + ")");
                            }
                        }
                    }
                }
            }

            // 🛒 상품 등록 (지역 포함)
            String sql = """
                INSERT INTO products (
                    seller_id, category_id, title, status,
                    sell_price, description, sido_id, region_id, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
            """;

            int newProductId;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, loginUserId);
                ps.setInt(2, categoryId);
                ps.setString(3, title);
                ps.setString(4, status);
                ps.setInt(5, sellPrice);
                ps.setString(6, description);

                // 📍 지역 정보 저장
                if (sidoAreaIdStr != null && !sidoAreaIdStr.isEmpty()) {
                    ps.setInt(7, Integer.parseInt(sidoAreaIdStr));
                } else {
                    ps.setNull(7, Types.INTEGER);
                }

                if (siggAreaIdStr != null && !siggAreaIdStr.isEmpty()) {
                    ps.setInt(8, Integer.parseInt(siggAreaIdStr));
                } else {
                    ps.setNull(8, Types.INTEGER);
                }

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("상품 ID 생성 실패");
                    newProductId = rs.getInt(1);
                }
            }

            // 🖼️ 이미지 저장
            for (Part part : req.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = UUID.randomUUID() + "_" + part.getSubmittedFileName();
                    File file = new File(uploadDir, fileName);
                    part.write(file.getAbsolutePath());

                    saveImageRecord(conn, newProductId, loginUserId, fileName);
                    System.out.println("✅ 이미지 저장 완료: " + file.getAbsolutePath());
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("상품 등록 중 DB 오류 발생", e);
        }

        resp.sendRedirect(req.getContextPath() + "/product/list");
    }

    // 🖼️ 이미지 DB 기록 메서드
    private void saveImageRecord(Connection conn, int productId, int uploaderId, String imgSrc) throws SQLException {
        String insertImg = "INSERT INTO images (uploader_id, name) VALUES (?, ?)";
        String insertMap = "INSERT INTO product_images (product_id, image_id) VALUES (?, ?)";

        try (PreparedStatement ps1 = conn.prepareStatement(insertImg, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps1.setInt(1, uploaderId);
            ps1.setString(2, imgSrc);
            ps1.executeUpdate();

            try (ResultSet rs = ps1.getGeneratedKeys()) {
                if (rs.next()) {
                    int imgId = rs.getInt(1);
                    try (PreparedStatement ps2 = conn.prepareStatement(insertMap)) {
                        ps2.setInt(1, productId);
                        ps2.setInt(2, imgId);
                        ps2.executeUpdate();
                    }
                }
            }
        }
    }
}

package web;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Product;

@WebServlet("/product/update")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // 10MB
public class ProductUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/product/list");
            return;
        }

        int productId = Integer.parseInt(idStr);

        try (Connection conn = DBUtil.getConnection()) {

            // ✅ 1️⃣ 상품 조회
            String sql = """
                SELECT id, title, description, sell_price, category_id, status, seller_id
                FROM products
                WHERE id = ?
            """;

            Product product = null;
            String regionSql = """
            	    SELECT sa.id AS sido_id, sa.name AS sido_name,
            	           sg.id AS sigg_id, sg.name AS sigg_name
            	    FROM activity_areas aa
            	    JOIN sigg_areas sg ON aa.sigg_area_id = sg.id
            	    JOIN sido_areas sa ON sg.sido_area_id = sa.id
            	    WHERE aa.user_id = ?
            	    LIMIT 1
            	""";
            try (PreparedStatement ps = conn.prepareStatement(regionSql)) {
                ps.setInt(1, product.getSellerId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        req.setAttribute("sidoId", rs.getInt("sido_id"));
                        req.setAttribute("regionId", rs.getInt("sigg_id"));
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        product = new Product();
                        product.setId(rs.getInt("id"));
                        product.setTitle(rs.getString("title"));
                        product.setDescription(rs.getString("description"));
                        product.setSellPrice(rs.getInt("sell_price"));
                        product.setCategoryId(rs.getInt("category_id"));
                        product.setStatus(rs.getString("status"));
                        product.setSellerId(rs.getInt("seller_id"));
                    }
                }
            }

            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/product/list");
                return;
            }

            // ✅ 2️⃣ 시/도 목록 불러오기
            List<Map<String, Object>> sidoList = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM sido_areas ORDER BY name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("name", rs.getString("name"));
                    sidoList.add(map);
                }
            }
            req.setAttribute("sidoList", sidoList);

            // ✅ 3️⃣ 시군구 목록 (지금은 전체, 나중에 시도별 필터 가능)
            List<Map<String, Object>> siggList = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM sigg_areas ORDER BY name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("name", rs.getString("name"));
                    siggList.add(map);
                }
            }
            req.setAttribute("siggList", siggList);

            // ✅ 4️⃣ 카테고리 목록 불러오기
            List<Map<String, Object>> categoryList = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM categories ORDER BY name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("name", rs.getString("name"));
                    categoryList.add(map);
                }
            }

            req.setAttribute("categoryList", categoryList);
            req.setAttribute("product", product);

            // ✅ 수정 폼으로 포워딩
            req.getRequestDispatcher("/product/product_form.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/product/list");
        }

    }

    /**
     * ✅ [POST] 수정 처리
     * URL: /product/update
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // ✅ 세션 검사
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

        // ✅ 폼 데이터 수집
        int productId = parseInt(req.getParameter("id"), 0);
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        int sellPrice = parseInt(req.getParameter("sellPrice"), 0);
        int categoryId = parseInt(req.getParameter("categoryId"), 0);
        String status = req.getParameter("status");

        // ✅ 업로드 경로
        String uploadPath = req.getServletContext().getRealPath("/upload/product_images/");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // ✅ 상품 수정
            String sql = """
                UPDATE products
                   SET title = ?, category_id = ?, sell_price = ?, description = ?, status = ?
                 WHERE id = ? AND seller_id = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setInt(2, categoryId);
                ps.setInt(3, sellPrice);
                ps.setString(4, description);
                ps.setString(5, status);
                ps.setInt(6, productId);
                ps.setInt(7, loginUserId);
                ps.executeUpdate();
            }

            // ✅ 기존 이미지 제거
            deleteOldImages(conn, productId);

            // ✅ 새 이미지 업로드
            for (Part part : req.getParts()) {
                if ("images".equals(part.getName()) && part.getSize() > 0) {
                    String fileName = UUID.randomUUID() + "_" + part.getSubmittedFileName();
                    File file = new File(uploadDir, fileName);
                    part.write(file.getAbsolutePath());

                    String imgSrc = "/upload/product_images/" + fileName;
                    saveImageRecord(conn, productId, loginUserId, imgSrc);
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 수정 중 오류 발생", e);
        }

        // ✅ 상세 페이지로 이동
        resp.sendRedirect(req.getContextPath() + "/product/detail?id=" + productId);
    }

    // ✅ 숫자 안전 파싱
    private int parseInt(String val, int defaultVal) {
        try {
            return val != null && !val.isEmpty() ? Integer.parseInt(val) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    // ✅ 기존 이미지 삭제
    private void deleteOldImages(Connection conn, int productId) throws SQLException {
        String deleteMap = "DELETE FROM product_images WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteMap)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    // ✅ 이미지 저장
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

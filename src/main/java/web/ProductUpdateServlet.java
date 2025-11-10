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
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) 
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

            String sql = """
                SELECT id, title, description, sell_price, category_id, status, seller_id, sido_id, region_id
                FROM products
                WHERE id = ?
            """;

            Product product = null;

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
                        product.setSidoId(rs.getInt("sido_id"));
                        product.setRegionId(rs.getInt("region_id"));
                    }
                }
            }

            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/product/list");
                return;
            }

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
            req.getRequestDispatcher("/product/product_form.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/product/list");
        }
    }

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

        int productId = parseInt(req.getParameter("id"), 0);
        if (productId <= 0) {
            throw new ServletException("상품 ID가 유효하지 않습니다.");
        }

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        int sellPrice = parseInt(req.getParameter("sellPrice"), 0);
        int categoryId = parseInt(req.getParameter("categoryId"), 0);
        String status = req.getParameter("status");
        int sidoId = parseInt(req.getParameter("sidoId"), 0);
        int regionId = parseInt(req.getParameter("regionId"), 0);
        String uploadPath = "D:/upload/product_images";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM products WHERE id = ?")) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ServletException("존재하지 않는 상품입니다. id=" + productId);
                    }
                }
            }

            String sql = """
                UPDATE products
                   SET title = ?, category_id = ?, sell_price = ?, description = ?, status = ?, sido_id = ?, region_id = ?
                 WHERE id = ? AND seller_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setInt(2, categoryId);
                ps.setInt(3, sellPrice);
                ps.setString(4, description);
                ps.setString(5, status);
                if (sidoId > 0) ps.setInt(6, sidoId); else ps.setNull(6, Types.INTEGER);
                if (regionId > 0) ps.setInt(7, regionId); else ps.setNull(7, Types.INTEGER);
                ps.setInt(8, productId);
                ps.setInt(9, loginUserId);
                ps.executeUpdate();
            }

            deleteOldImages(conn, productId, uploadPath);

            for (Part part : req.getParts()) {
                if ("images".equals(part.getName()) && part.getSize() > 0) {
                    String submittedFileName = part.getSubmittedFileName();
                    String fileName = UUID.randomUUID() + "_" + submittedFileName;

                    File saveFile = new File(uploadDir, fileName);
                    part.write(saveFile.getAbsolutePath());

                    saveImageRecord(conn, productId, loginUserId, fileName);
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 수정 중 오류 발생", e);
        }

        resp.sendRedirect(req.getContextPath() + "/product/detail?id=" + productId);
    }

    private int parseInt(String val, int defaultVal) {
        try {
            return val != null && !val.isEmpty() ? Integer.parseInt(val) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /** ✅ 기존 이미지 실제 파일 + DB 매핑 삭제 */
    private void deleteOldImages(Connection conn, int productId, String uploadPath) throws SQLException {
        String selectImgSql = """
            SELECT i.name
              FROM images i
              JOIN product_images pi ON i.id = pi.image_id
             WHERE pi.product_id = ?
        """;

        List<String> oldFiles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(selectImgSql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    oldFiles.add(rs.getString("name"));
                }
            }
        }

        for (String fileName : oldFiles) {
            File f = new File(uploadPath, fileName);
            if (f.exists() && f.delete()) {
                System.out.println("✅ 이미지 파일 삭제됨: " + f.getAbsolutePath());
            } else {
                System.out.println("⚠️ 이미지 파일 삭제 실패 또는 없음: " + f.getAbsolutePath());
            }
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product_images WHERE product_id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM images WHERE id NOT IN (SELECT image_id FROM product_images)")) {
            ps.executeUpdate();
        }
    }

    private void saveImageRecord(Connection conn, int productId, int uploaderId, String imgSrc) throws SQLException {
        String insertImg = "INSERT INTO images (uploader_id, name) VALUES (?, ?)";
        String insertMap = "INSERT INTO product_images (product_id, image_id) VALUES (?, ?)";

        try (PreparedStatement ps1 = conn.prepareStatement(insertImg, Statement.RETURN_GENERATED_KEYS)) {
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

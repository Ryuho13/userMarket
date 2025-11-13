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

        int productId;
        try {
            productId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/product/list");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {

            // 1) 상품 정보 조회
            String sql = """
                SELECT id, title, description, sell_price, category_id,
                       status, seller_id, sido_id, region_id
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

            // ✔ 2) 상품 이미지 조회
            List<String> productImages = new ArrayList<>();

            String imgSql = """
                SELECT i.name
                  FROM images i
                  JOIN product_images pi ON i.id = pi.image_id
                 WHERE pi.product_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(imgSql)) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        productImages.add(rs.getString("name"));
                    }
                }
            }

            req.setAttribute("productImages", productImages);

            // 3) 지역, 카테고리 조회
            List<Map<String, Object>> sidoList = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name FROM sido_areas ORDER BY name");
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
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name FROM sigg_areas ORDER BY name");
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
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name FROM categories ORDER BY name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("name", rs.getString("name"));
                    categoryList.add(map);
                }
            }
            req.setAttribute("categoryList", categoryList);

            // 4) JSP로 전달
            req.setAttribute("product", product);
            req.getRequestDispatcher("/product/product_form.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/product/list");
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int productId = Integer.parseInt(req.getParameter("id"));
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        int sellPrice = Integer.parseInt(req.getParameter("sellPrice"));
        int categoryId = Integer.parseInt(req.getParameter("categoryId"));
        String status = req.getParameter("status");
        int sidoId = Integer.parseInt(req.getParameter("sidoId"));
        int regionId = Integer.parseInt(req.getParameter("regionId"));

        try (Connection conn = DBUtil.getConnection()) {

        	// 0) uploaderId (항상 seller_id 사용)
        	int uploaderId = 0;

        	String sSql = "SELECT seller_id FROM products WHERE id=?";
        	try (PreparedStatement ps = conn.prepareStatement(sSql)) {
        	    ps.setInt(1, productId);
        	    ResultSet rs = ps.executeQuery();
        	    if (rs.next()) {
        	        uploaderId = rs.getInt("seller_id");
        	    }
        	}

            String sql = """
                UPDATE products
                   SET title=?, description=?, sell_price=?, category_id=?,
                       status=?, sido_id=?, region_id=?
                 WHERE id=?
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setInt(3, sellPrice);
                ps.setInt(4, categoryId);
                ps.setString(5, status);
                ps.setInt(6, sidoId);
                ps.setInt(7, regionId);
                ps.setInt(8, productId);
                ps.executeUpdate();
            }

            Collection<Part> parts = req.getParts();

            for (Part part : parts) {
                if (part.getName().equals("images") && part.getSize() > 0) {

                    String fileName = UUID.randomUUID() + "_" + part.getSubmittedFileName();
                    String uploadPath = "D:/upload/product_images/" + fileName;

                    part.write(uploadPath);

                    // images 테이블 저장
                    int imageId = 0;
                    String imgSql = "INSERT INTO images (name, uploader_id) VALUES (?, ?)";

                    try (PreparedStatement ps = conn.prepareStatement(imgSql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, fileName);
                        ps.setInt(2, uploaderId);
                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) imageId = rs.getInt(1);
                    }

                    // product_images 매핑 저장
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO product_images(product_id, image_id) VALUES (?, ?)")) {
                        ps.setInt(1, productId);
                        ps.setInt(2, imageId);
                        ps.executeUpdate();
                    }
                }
            }

            resp.sendRedirect(req.getContextPath() + "/product/detail?id=" + productId);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/product/update?id=" + productId + "&error=1");
        }
    }



}

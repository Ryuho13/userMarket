package web;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.User;

@WebServlet("/product/update")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // 10MB
public class ProductUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");

        String idStr = req.getParameter("id");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String sellPriceStr = req.getParameter("sellPrice");
        String categoryIdStr = req.getParameter("categoryId");
        String status = req.getParameter("status");

        int productId = Integer.parseInt(idStr);
        int sellPrice = sellPriceStr != null && !sellPriceStr.isEmpty() ? Integer.parseInt(sellPriceStr) : 0;
        int categoryId = Integer.parseInt(categoryIdStr);

        // ✅ 이미지 업로드 처리
        String uploadPath = req.getServletContext().getRealPath("/product/resources/images/");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String imgSrc = null;
        for (Part part : req.getParts()) {
            if (part.getName().equals("images") && part.getSize() > 0) {
                String fileName = UUID.randomUUID() + "_" + part.getSubmittedFileName();
                File file = new File(uploadDir, fileName);
                part.write(file.getAbsolutePath());
                imgSrc = "/resources/upload/product/" + fileName;
                break;
            }
        }

        String sql = """
            UPDATE products
               SET title = ?, category_id = ?, status = ?, sell_price = ?, description = ?
             WHERE id = ? AND seller_id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setInt(2, categoryId);
            ps.setString(3, status);
            ps.setInt(4, sellPrice);
            ps.setString(5, description);
            ps.setInt(6, productId);
            ps.setInt(7, loginUser.getId());
            ps.executeUpdate();

            // ✅ 이미지 변경이 있다면 새 경로 저장
            if (imgSrc != null) {
                saveImageRecord(conn, productId, loginUser.getId(), imgSrc);
            }

        } catch (SQLException e) {
            throw new ServletException("상품 수정 중 DB 오류 발생", e);
        }

        resp.sendRedirect(req.getContextPath() + "/product/detail?id=" + productId);
    }

    private void saveImageRecord(Connection conn, int productId, int uploaderId, String imgSrc) throws SQLException {
        String insertImg = "INSERT INTO images (uploader_id, name) VALUES (?, ?)";
        String insertMap = "INSERT INTO product_images (product_id, image_id) VALUES (?, ?)";

        try (PreparedStatement ps1 = conn.prepareStatement(insertImg, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps1.setInt(1, uploaderId);
            ps1.setString(2, imgSrc);
            ps1.executeUpdate();

            try (var rs = ps1.getGeneratedKeys()) {
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

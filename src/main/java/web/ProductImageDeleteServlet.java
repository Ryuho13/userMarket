package web;

import dao.DBUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;

@WebServlet("/product/deleteImage")
public class ProductImageDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");

        String imgName = req.getParameter("imgName");
        String productIdStr = req.getParameter("productId");

        if (imgName == null || productIdStr == null) {
            resp.getWriter().write("NO_DATA");
            return;
        }

        int productId = Integer.parseInt(productIdStr);

        try (Connection conn = DBUtil.getConnection()) {

            String sql = """
                SELECT i.id
                FROM images i
                JOIN product_images pi ON pi.image_id = i.id
                WHERE pi.product_id = ? AND i.name = ?
            """;

            int imgId = -1;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setString(2, imgName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        imgId = rs.getInt("id");
                    }
                }
            }

            if (imgId == -1) {
                resp.getWriter().write("NOT_FOUND");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM product_images WHERE product_id = ? AND image_id = ?"
            )) {
                ps.setInt(1, productId);
                ps.setInt(2, imgId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM images WHERE id = ?"
            )) {
                ps.setInt(1, imgId);
                ps.executeUpdate();
            }

            File f = new File("D:/upload/product_images", imgName);
            if (f.exists()) f.delete();

            resp.getWriter().write("OK");

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("ERROR");
        }
    }
}

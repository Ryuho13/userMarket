package dao;

import model.ProductDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailDAO {

    // ✅ 조회수 1 증가
    public void increaseViewCount(int productId) throws SQLException {
        String sql = "UPDATE products SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public ProductDetail findById(int productId) throws Exception {
        ProductDetail pd = null;

        // ✅ 1. 상품 기본 정보 + 카테고리 ID
        String sql = """
            SELECT id, title, description, sell_price, seller_id, status, category_id
            FROM products
            WHERE id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pd = new ProductDetail();
                    pd.setId(rs.getInt("id"));
                    pd.setTitle(rs.getString("title"));
                    pd.setDescription(rs.getString("description"));
                    pd.setSellPrice(rs.getInt("sell_price"));
                    pd.setSellerId(rs.getInt("seller_id"));
                    pd.setStatus(rs.getString("status"));
                    pd.setCategoryId(rs.getInt("category_id")); // ✅ 추가됨 (가장 중요)
                }
            }
        }

        if (pd == null) return null;

     // ✅ 2. 상품 이미지
        List<String> images = new ArrayList<>();
        String imgSql = """
            SELECT i.name
            FROM product_images pi
            JOIN images i ON i.id = pi.image_id
            WHERE pi.product_id = ?
        """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(imgSql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String imgName = rs.getString("name");

                    if (imgName != null && !imgName.isBlank()) {
                        // ✅ /userMarket/upload 경로를 포함하도록 변경
                        if (!imgName.startsWith("/userMarket/upload/")) {
                            imgName =  imgName;
                        }
                    } else {
                        imgName = "/userMarket/resources/images/noimage.jpg";
                    }

                    images.add(imgName);
                }
            }
        }
        pd.setImages(images);


        
        // ✅ 3. 판매자 연락처
        String sellerSql = """
            SELECT phn
            FROM `user`
            WHERE id = ?
        """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sellerSql)) {

            ps.setInt(1, pd.getSellerId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pd.setSellerMobile(rs.getString("phn"));
                }
            }
        }

        // ✅ 4. 판매자 활동 지역
        String locSql = """
            SELECT sa.name AS sigg_name
            FROM activity_areas aa
            JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            WHERE aa.user_id = ?
            LIMIT 1
        """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(locSql)) {

            ps.setInt(1, pd.getSellerId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pd.setSellerSigg(rs.getString("sigg_name"));
                }
            }
        }

        return pd;
    }
}

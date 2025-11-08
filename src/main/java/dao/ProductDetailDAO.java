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

    // ✅ 상품 상세 조회 (지역 + 판매자 + 이미지)
    public ProductDetail findById(int productId) throws SQLException {
        String sql = """
            SELECT p.*, 
                   s.name AS sido_name,
                   g.name AS region_name,
                   u.name AS seller_name,
                   u.phn AS seller_mobile
              FROM products p
              LEFT JOIN sido_areas s ON p.sido_id = s.id
              LEFT JOIN sigg_areas g ON p.region_id = g.id
              JOIN user u ON p.seller_id = u.id
             WHERE p.id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                ProductDetail pd = new ProductDetail();
                pd.setId(rs.getInt("id"));
                pd.setSellerId(rs.getInt("seller_id"));
                pd.setCategoryId(rs.getInt("category_id"));
                pd.setTitle(rs.getString("title"));
                pd.setStatus(rs.getString("status"));
                pd.setSellPrice(rs.getInt("sell_price"));
                pd.setDescription(rs.getString("description"));
                pd.setCreatedAt(rs.getTimestamp("created_at"));
                pd.setSidoName(rs.getString("sido_name"));
                pd.setRegionName(rs.getString("region_name"));
                pd.setSellerName(rs.getString("seller_name"));
                pd.setSellerMobile(rs.getString("seller_mobile"));

                // ✅ 이미지 목록 불러오기
                pd.setImages(loadProductImages(conn, productId));

                return pd;
            }
        }
    }

    // ✅ 상품 이미지 로드
    private List<String> loadProductImages(Connection conn, int productId) throws SQLException {
        List<String> images = new ArrayList<>();
        String sql = """
            SELECT i.name
              FROM product_images pi
              JOIN images i ON pi.image_id = i.id
             WHERE pi.product_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    images.add(rs.getString("name"));
                }
            }
        }
        return images;
    }

    // ✅ 찜 여부 확인
    public boolean isWished(int userId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM wish_lists WHERE register_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}

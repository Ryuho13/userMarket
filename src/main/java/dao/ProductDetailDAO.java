package dao;

import model.ProductDetail;
import model.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailDAO {

    /** ✅ 조회수 증가 */
    public void increaseViewCount(int productId) throws SQLException {
        String sql = "UPDATE products SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    /** ✅ 조회수 증가 + 상세조회 (트랜잭션 처리) */
    public ProductDetail incrementAndFindById(int productId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            boolean old = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                // 조회수 증가
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE products SET view_count = view_count + 1 WHERE id = ?")) {
                    ps.setInt(1, productId);
                    ps.executeUpdate();
                }

                // 상품 + 판매자 + 카테고리 + 평점 + 리뷰 로딩
                ProductDetail pd = findById(conn, productId);

                conn.commit();
                conn.setAutoCommit(old);
                return pd;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /** ✅ 상품 상세 조회 */
    public ProductDetail findById(int productId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            return findById(conn, productId);
        }
    }

    private ProductDetail findById(Connection conn, int productId) throws SQLException {
        String sql = """
            SELECT p.*, 
                   s.name AS sido_name,
                   g.name AS region_name,
                   u.name AS seller_name,
                   u.phn  AS seller_mobile,
                   c.name AS category_name
              FROM products p
              LEFT JOIN sido_areas s ON p.sido_id = s.id
              LEFT JOIN sigg_areas g ON p.region_id = g.id
              JOIN user u ON p.seller_id = u.id  
              JOIN categories c ON p.category_id = c.id
             WHERE p.id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
                pd.setViewCount(rs.getInt("view_count"));
                pd.setSidoName(rs.getString("sido_name"));
                pd.setRegionName(rs.getString("region_name"));
                pd.setSellerName(rs.getString("seller_name"));
                pd.setSellerMobile(rs.getString("seller_mobile"));
                pd.setCategoryName(rs.getString("category_name"));

                // ✅ 이미지 리스트
                pd.setImages(loadProductImages(conn, productId));

                // ✅ 판매자 평점 평균/개수
                loadSellerRating(conn, pd);

                // ✅ 판매자 리뷰 리스트 추가
                pd.setReviews(loadSellerReviews(conn, pd.getSellerId()));

                return pd;
            }
        }
    }

    /** ✅ 상품 이미지 로딩 */
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

    /** ✅ 판매자 평점 정보 로딩 */
    private void loadSellerRating(Connection conn, ProductDetail pd) throws SQLException {
        String sql = """
            SELECT AVG(rating) AS avg_rating,
                   COUNT(*)    AS cnt
              FROM seller_ratings
             WHERE seller_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pd.getSellerId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_rating");
                    int count = rs.getInt("cnt");
                    if (rs.wasNull() || count == 0) {
                        pd.setSellerRating(null);
                        pd.setSellerRatingCount(0);
                    } else {
                        pd.setSellerRating(avg);
                        pd.setSellerRatingCount(count);
                    }
                }
            }
        }
    }

    /** ✅ 판매자 리뷰 리스트 로딩 */
    private List<Review> loadSellerReviews(Connection conn, int sellerId) throws SQLException {
        List<Review> reviews = new ArrayList<>();

        String sql = """
            SELECT r.rating,
                   r.comment,
                   r.created_at,
                   b.name  AS buyer_name,
                   p.id    AS product_id,
                   p.title AS product_title
              FROM seller_ratings r
              JOIN `user`   b ON b.id = r.buyer_id      
              JOIN products p ON p.id = r.product_id
             WHERE r.seller_id = ?
             ORDER BY r.created_at DESC
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setCreatedAt(rs.getTimestamp("created_at"));
                    review.setBuyerName(rs.getString("buyer_name"));

                    // ✅ 어떤 상품에 대한 리뷰인지 세팅
                    int pid = rs.getInt("product_id");
                    review.setProductId(rs.wasNull() ? null : pid);  
                    review.setProductTitle(rs.getString("product_title"));

                    reviews.add(review);
                }
            }
        }

        return reviews;
    }


    /** ✅ 찜 여부 확인 */
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

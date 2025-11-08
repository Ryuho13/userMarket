package dao;

import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ─────────────────────────────
    // 공통 헬퍼
    // ─────────────────────────────

    // 이미지 경로 보정(중복 로직 통일)
    private static String normalizeDisplayImg(String imgName) {
        if (imgName == null || imgName.isEmpty()) {
            return "/product/resources/images/noimage.jpg";
        }
        String display = imgName.replace("/userMarket", "");
        if (!display.contains("/upload/product_images/")) {
            display = "/upload/product_images/" + display;
        }
        return display;
    }

    // LIKE 검색어 이스케이프 (% _ \)
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    // ─────────────────────────────
    // 단건 조회
    // ─────────────────────────────

    // ✅ 특정 상품 상세 조회
    public Product getProductById(int id) throws SQLException {
        String sql = """
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   p.view_count, COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            WHERE p.id = ?
            GROUP BY p.id, p.title, p.sell_price, p.status, p.view_count, sa.name
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    return new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getInt("view_count"),
                            rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    // ✅ 조회수 1 증가
    public void increaseViewCount(int id) throws SQLException {
        String sql = "UPDATE products SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ─────────────────────────────
    // 목록/카운트
    // ─────────────────────────────

    // ✅ 상품 개수 카운트
    public int countProducts() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM products";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("cnt") : 0;
        }
    }

    // ✅ 상품 목록 조회
    public List<Product> listProducts(int offset, int size) throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = """
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price,
                   p.status, COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            GROUP BY p.id, p.title, p.sell_price, p.status, sa.name
            ORDER BY p.id DESC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name")); // ✅ 누락 수정
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    // ─────────────────────────────
    // 검색(문자열 기반 + 페이징 오버로드)
    // ─────────────────────────────

    // ✅ 기존 시그니처 유지(전부 반환). 내부적으로 페이징 오버로드 호출
    public List<Product> searchProducts(String q, String sigg, String category) throws SQLException {
        return searchProducts(q, sigg, category, 0, Integer.MAX_VALUE);
    }

    // ✅ 검색(문자열 기반, 페이징 가능, LIKE ESCAPE 적용)
    public List<Product> searchProducts(String q, String sigg, String category,
                                        int offset, int size) throws SQLException {

        List<Product> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT p.id AS product_id, p.title AS product_name, p.status, p.sell_price,
                   COALESCE(sa.name, '지역정보없음') AS sigg_name, MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            sql.append(" AND (p.title LIKE ? ESCAPE '\\\\' OR c.name LIKE ? ESCAPE '\\\\') ");
            String like = "%" + escapeLike(q.trim()) + "%";
            params.add(like);
            params.add(like);
        }
        if (sigg != null && !sigg.trim().isEmpty()) {
            sql.append(" AND sa.name = ? ");
            params.add(sigg.trim());
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND c.name = ? ");
            params.add(category.trim());
        }

        sql.append(" GROUP BY p.id, p.title, p.status, p.sell_price, sa.name ");
        sql.append(" ORDER BY p.id DESC ");
        sql.append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add(offset);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            bind(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    // ✅ 검색 총 개수 (문자열 기반)
    public int countSearchProducts(String q, String sigg, String category) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.id) AS cnt
            FROM products p
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            sql.append(" AND (p.title LIKE ? ESCAPE '\\\\' OR c.name LIKE ? ESCAPE '\\\\') ");
            String like = "%" + escapeLike(q.trim()) + "%";
            params.add(like);
            params.add(like);
        }
        if (sigg != null && !sigg.trim().isEmpty()) {
            sql.append(" AND sa.name = ? ");
            params.add(sigg.trim());
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND c.name = ? ");
            params.add(category.trim());
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }

    // ─────────────────────────────
    // 필터(문자열 기반)
    // ─────────────────────────────

    // ✅ 가격/지역/카테고리 필터 기능
    public List<Product> getFilteredProducts(String category, String region, Integer minPrice, Integer maxPrice,
                                             int offset, int size) throws Exception {
        List<Product> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   COALESCE(sa.name, '지역정보없음') AS sigg_name, MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            sql.append(" AND c.name = ?");
            params.add(category);
        }
        if (region != null && !region.isEmpty()) {
            sql.append(" AND sa.name = ?");
            params.add(region);
        }
        if (minPrice != null) {
            sql.append(" AND p.sell_price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND p.sell_price <= ?");
            params.add(maxPrice);
        }

        sql.append("""
            GROUP BY p.id, p.title, p.sell_price, p.status, sa.name
            ORDER BY p.id DESC
            LIMIT ? OFFSET ?
        """);
        params.add(size);
        params.add(offset);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            bind(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    // ✅ 필터된 상품 개수 카운트
    public int countFilteredProducts(String category, String region, Integer minPrice, Integer maxPrice) throws Exception {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.id) AS cnt
            FROM products p
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            sql.append(" AND c.name = ?");
            params.add(category);
        }
        if (region != null && !region.isEmpty()) {
            sql.append(" AND sa.name = ?");
            params.add(region);
        }
        if (minPrice != null) {
            sql.append(" AND p.sell_price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND p.sell_price <= ?");
            params.add(maxPrice);
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            bind(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }

    // ─────────────────────────────
    // 연관 상품
    // ─────────────────────────────

    // ✅ 같은 카테고리의 다른 상품 (현재 상품 제외)
    public List<Product> getProductsByCategory(int categoryId, int excludeId) throws SQLException {
        String sql = """
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            WHERE p.category_id = ? AND p.id <> ?
            GROUP BY p.id, p.title, p.sell_price, p.status, sa.name
            ORDER BY p.id DESC
        """;

        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    // ✅ 같은 판매자의 다른 상품 (현재 상품 제외)
    public List<Product> getProductsBySeller(int sellerId, int excludeId) throws SQLException {
        String sql = """
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            WHERE p.seller_id = ? AND p.id <> ?
            GROUP BY p.id, p.title, p.sell_price, p.status, sa.name
            ORDER BY p.id DESC
        """;

        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ps.setInt(2, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    // ─────────────────────────────
    // ✅ (수정완료) ID 기반 오버로드 2개
    // ─────────────────────────────

    // 총 개수 (ESCAPE + description 검색)
    public int countSearchProducts(String q, Integer categoryId, Integer siggAreaId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.id) AS cnt
              FROM products p
             WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            sql.append(" AND (p.title LIKE ? ESCAPE '\\\\' OR p.description LIKE ? ESCAPE '\\\\') ");
            String like = "%" + escapeLike(q.trim()) + "%";
            params.add(like);
            params.add(like);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (siggAreaId != null && siggAreaId > 0) {
            sql.append(" AND p.region_id = ? ");
            params.add(siggAreaId);
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // 목록 (ESCAPE + description 검색 + 이미지 경로 정규화)
    public List<Product> searchProducts(String q, Integer categoryId, Integer siggAreaId,
                                        int offset, int size) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT
                p.id AS product_id, p.title AS product_name, p.status, p.sell_price,
                COALESCE(sa.name, '지역정보없음') AS sigg_name,
                ( SELECT i.name
                    FROM product_images pi
                    JOIN images i ON pi.image_id = i.id
                   WHERE pi.product_id = p.id
                   ORDER BY pi.image_id
                   LIMIT 1
                ) AS img_name
              FROM products p
              LEFT JOIN sigg_areas sa ON p.region_id = sa.id
             WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            sql.append(" AND (p.title LIKE ? ESCAPE '\\\\' OR p.description LIKE ? ESCAPE '\\\\') ");
            String like = "%" + escapeLike(q.trim()) + "%";
            params.add(like);
            params.add(like);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (siggAreaId != null && siggAreaId > 0) {
            sql.append(" AND p.region_id = ? ");
            params.add(siggAreaId);
        }

        sql.append("""
             ORDER BY p.created_at DESC, p.id DESC
             LIMIT ? OFFSET ?
        """);
        params.add(size);
        params.add(offset);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> list = new ArrayList<>();
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getString("status")
                    ));
                }
                return list;
            }
        }
    }
 // ProductDAO.java

    public List<String> getPopularKeywords(int limit) throws SQLException {
        List<String> list = new ArrayList<>();

        String sql = """
            SELECT p.title
              FROM products p
             ORDER BY p.view_count DESC, p.id DESC
             LIMIT ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("title"));
                }
            }
        }
        return list;
    }

}

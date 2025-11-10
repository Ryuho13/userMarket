package dao;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

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

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    /** ✅ 단일 상품 조회 */
    public Product getProductById(int id) throws SQLException {
        String sql = """
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   p.view_count, COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN sigg_areas sa ON p.region_id = sa.id
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
    public List<Product> getProductsByCategory(int categoryId, int excludeId) throws SQLException {
        String sql = """
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN sigg_areas sa ON p.region_id = sa.id
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
    public List<Product> getProductsBySeller(int sellerId, int excludeId) throws SQLException {
        String sql = """
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN images i ON pi.image_id = i.id
            LEFT JOIN sigg_areas sa ON p.region_id = sa.id
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

    /** ✅ 검색 + 정렬 */
    public List<Product> searchProducts(String q, Integer categoryId, Integer siggAreaId,
                                        int offset, int size, String sort) throws SQLException {

        StringBuilder sql = new StringBuilder("""
            SELECT p.id AS product_id, p.title AS product_name, p.status, p.sell_price,
                   p.view_count, COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   (SELECT i.name FROM product_images pi
                     JOIN images i ON pi.image_id = i.id
                     WHERE pi.product_id = p.id
                     ORDER BY pi.image_id LIMIT 1) AS img_name
              FROM products p
              LEFT JOIN sigg_areas sa ON p.region_id = sa.id
             WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
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

        // ✅ 정렬 옵션
        switch (sort) {
            case "view": sql.append(" ORDER BY p.view_count DESC "); break;
            case "name": sql.append(" ORDER BY p.title ASC "); break;
            case "priceLow": sql.append(" ORDER BY p.sell_price ASC "); break;
            case "priceHigh": sql.append(" ORDER BY p.sell_price DESC "); break;
            default: sql.append(" ORDER BY p.created_at DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add(offset);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            bind(ps, params);
            List<Product> list = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getInt("view_count"),
                            rs.getString("status")
                    ));
                }
            }
            return list;
        }
    }

    /** ✅ 필터 + 정렬 */
    public List<Product> getFilteredProducts(String category, String region, Integer minPrice, Integer maxPrice,
                                             int offset, int size, String sort) throws Exception {

        StringBuilder sql = new StringBuilder("""
            SELECT p.id AS product_id, p.title AS product_name, p.sell_price, p.status,
                   p.view_count, COALESCE(sa.name, '지역정보없음') AS sigg_name, MIN(i.name) AS img_name
              FROM products p
              LEFT JOIN product_images pi ON p.id = pi.product_id
              LEFT JOIN images i ON pi.image_id = i.id
              LEFT JOIN sigg_areas sa ON p.region_id = sa.id
              LEFT JOIN categories c ON p.category_id = c.id
             WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            sql.append(" AND c.name = ? ");
            params.add(category);
        }
        if (region != null && !region.isEmpty()) {
            sql.append(" AND sa.name = ? ");
            params.add(region);
        }
        if (minPrice != null) {
            sql.append(" AND p.sell_price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND p.sell_price <= ? ");
            params.add(maxPrice);
        }

        sql.append(" GROUP BY p.id, p.title, p.sell_price, p.status, sa.name ");

        // ✅ 정렬
        switch (sort) {
            case "view": sql.append(" ORDER BY p.view_count DESC "); break;
            case "name": sql.append(" ORDER BY p.title ASC "); break;
            case "priceLow": sql.append(" ORDER BY p.sell_price ASC "); break;
            case "priceHigh": sql.append(" ORDER BY p.sell_price DESC "); break;
            default: sql.append(" ORDER BY p.created_at DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add(offset);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            bind(ps, params);
            List<Product> list = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String displayImg = normalizeDisplayImg(rs.getString("img_name"));
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg,
                            rs.getInt("view_count"),
                            rs.getString("status")
                    ));
                }
            }
            return list;
        }
    }

    /** ✅ 검색/필터용 카운트 */
    public int countSearchProducts(String q, Integer categoryId, Integer siggAreaId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.id) AS cnt
              FROM products p
             WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
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
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }

    public int countFilteredProducts(String category, String region, Integer minPrice, Integer maxPrice) throws Exception {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.id) AS cnt
              FROM products p
              LEFT JOIN sigg_areas sa ON p.region_id = sa.id
              LEFT JOIN categories c ON p.category_id = c.id
             WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            sql.append(" AND c.name = ? ");
            params.add(category);
        }
        if (region != null && !region.isEmpty()) {
            sql.append(" AND sa.name = ? ");
            params.add(region);
        }
        if (minPrice != null) {
            sql.append(" AND p.sell_price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND p.sell_price <= ? ");
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
}

package dao;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

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
                   COALESCE(sa.name, '지역정보없음') AS sigg_name,
                   MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN products_images pi ON p.id = pi.products_id
            LEFT JOIN imgs i ON pi.img_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            GROUP BY p.id, p.title, p.sell_price, sa.name
            ORDER BY p.id DESC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String imgName = rs.getString("img_name");
                    String displayImg = (imgName != null)
                            ? "/product/resources/images/" + imgName
                            : "/product/resources/images/noimage.jpg";

                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg
                    ));
                }
            }
        }
        return list;
    }

    // ✅ 상품 검색 기능
    public List<Product> searchProducts(String q, String sigg, String category)
            throws SQLException {

        List<Product> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT p.id AS product_id, p.title AS product_name, p.status, p.sell_price,
                   COALESCE(sa.name, '지역정보없음') AS sigg_name, MIN(i.name) AS img_name
            FROM products p
            LEFT JOIN products_images pi ON p.id = pi.products_id
            LEFT JOIN imgs i ON pi.img_id = i.id
            LEFT JOIN user u ON p.seller_id = u.id
            LEFT JOIN activity_areas aa ON u.id = aa.user_id
            LEFT JOIN sigg_areas sa ON aa.sigg_area_id = sa.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            sql.append(" AND (p.title LIKE ? OR c.name LIKE ?) ");
            params.add("%" + q.trim() + "%");
            params.add("%" + q.trim() + "%");
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

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String imgName = rs.getString("img_name");
                    String displayImg = (imgName != null)
                            ? "/product/resources/images/" + imgName
                            : "/product/resources/images/noimage.jpg";

                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("sell_price"),
                            rs.getString("sigg_name"),
                            displayImg
                    ));
                }
            }
        }
        return list;
    }
}

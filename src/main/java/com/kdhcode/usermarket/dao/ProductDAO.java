package com.kdhcode.usermarket.dao;

import com.kdhcode.usermarket.model.Product;
import com.kdhcode.usermarket.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public int countProducts() throws SQLException, ClassNotFoundException {
        String countSql =
            "SELECT COUNT(DISTINCT p.id) AS cnt " +
            "FROM products p " +
            "JOIN products_images pi ON p.id = pi.products_id " +
            "JOIN imgs i ON pi.img_id = i.id " +
            "JOIN users u ON p.seller_id = u.id " +
            "JOIN activity_areas aa ON u.id = aa.user_id " +
            "JOIN sigg_areas sa ON aa.id2 = sa.id " +
            "JOIN sido_areas s ON sa.sido_area_id = s.id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("cnt");
            return 0;
        }
    }

    public List<Product> listProducts(int offset, int size) throws SQLException, ClassNotFoundException {
        List<Product> list = new ArrayList<>();
        String sql =
            "SELECT p.id AS product_id, p.title AS product_name, p.status, p.sell_price, " +
            "sa.name AS sigg_name, MIN(i.name) AS img_name " +
            "FROM products p " +
            "JOIN products_images pi ON p.id = pi.products_id " +
            "JOIN imgs i ON pi.img_id = i.id " +
            "JOIN users u ON p.seller_id = u.id " +
            "JOIN activity_areas aa ON u.id = aa.user_id " +
            "JOIN sigg_areas sa ON aa.id2 = sa.id " +
            "JOIN sido_areas s ON sa.sido_area_id = s.id " +
            "GROUP BY p.id, p.title, p.status, p.sell_price, sa.name " +
            "ORDER BY p.created_at DESC " +
            "LIMIT ? OFFSET ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("product_id");
                    String name = rs.getString("product_name");
                    int price = rs.getInt("sell_price");
                    String sigg = rs.getString("sigg_name");
                    String img = rs.getString("img_name");
                    list.add(new Product(id, name, price, sigg, img));
                }
            }
        }
        return list;
    }
}

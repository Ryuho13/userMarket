package com.kdhcode.usermarket.dao;

import com.kdhcode.usermarket.model.ProductDetail;
import com.kdhcode.usermarket.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailDAO {

    public ProductDetail findById(int productId) throws Exception {
        ProductDetail pd = null;

        String sql = "SELECT id, title, description, sell_price, seller_id, status FROM products WHERE id = ?";
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
                }
            }
        }

        if (pd == null) return null;

        // load images
        List<String> images = new ArrayList<>();
        String imgSql = "SELECT i.name FROM imgs i JOIN products_images pi ON i.id = pi.img_id WHERE pi.products_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(imgSql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) images.add(rs.getString("name"));
            }
        }
        pd.setImages(images);

        // seller contact and rating
        String sellerSql = "SELECT mobile_number, rating_score FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sellerSql)) {
            ps.setInt(1, pd.getSellerId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pd.setSellerMobile(rs.getString("mobile_number"));
                    double r = rs.getDouble("rating_score");
                    if (!rs.wasNull()) pd.setSellerRating(r);
                }
            }
        }

        // seller sigg (location)
        String locSql = "SELECT sa.name AS sigg_name FROM activity_areas aa JOIN sigg_areas sa ON aa.id2 = sa.id WHERE aa.user_id = ? LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(locSql)) {
            ps.setInt(1, pd.getSellerId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) pd.setSellerSigg(rs.getString("sigg_name"));
            }
        }

        return pd;
    }
}

package dao;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> findAll() throws Exception {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        }
        return list;
    }

    public List<Category> getAllCategories() throws Exception {
        return findAll();
    }

    public Category getCategoryById(int id) throws Exception {
        String sql = "SELECT id, name FROM categories WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                }
            }
        }
        return null;
    }
}

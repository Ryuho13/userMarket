package dao;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    // ✅ 기존: 모든 카테고리 조회
    public List<Category> findAll() throws Exception {
        List<Category> list = new ArrayList<>();

        String sql = "SELECT id, name FROM categories ORDER BY id";
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

    // ✅ ProductListServlet 호환용 메서드 추가
    public List<Category> getAllCategories() throws Exception {
        return findAll();
    }
}

package com.kdhcode.usermarket.dao;

import com.kdhcode.usermarket.model.Category;
import com.kdhcode.usermarket.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<Category> listCategories() throws Exception {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM categories";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }
}

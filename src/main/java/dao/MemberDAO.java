package dao;

import dto.Member;
import java.sql.*;

public class MemberDAO {

    public boolean existsByEmail(String email) throws Exception {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByNickname(String nickname) throws Exception {
        String sql = "SELECT 1 FROM users WHERE nickname = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public long insert(Member m) throws Exception {
        String sql = "INSERT INTO users(email, password_hash, nickname, region_code) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getEmail());
            ps.setString(2, m.getPassword());  // 평문 저장 (요구사항)
            ps.setString(3, m.getNickname());
            ps.setString(4, m.getRegionCode());
            int rows = ps.executeUpdate();
            if (rows == 0) return 0;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        }
    }
}

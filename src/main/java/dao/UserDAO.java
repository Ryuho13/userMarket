package dao;

import java.sql.*;
import model.User;
import model.UserInfo;
import model.UserProfile;

public class UserDAO {

    public int createUserWithInfo(User user, UserInfo info) throws SQLException {
        String sqlUser = """
            INSERT INTO user (account_id, pw, name, phn, em, created_at)
            VALUES (?, ?, ?, ?, ?, NOW())
        """;

        String sqlInfo = """
            INSERT INTO user_info (u_id, nickname, region_id, addr_detail, profile_img)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);

                // user INSERT
                int newUserId;
                try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, user.getAccountId());
                    ps.setString(2, user.getPw());
                    ps.setString(3, user.getName());
                    ps.setString(4, user.getPhn());
                    ps.setString(5, user.getEm());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No generated key for user");
                        newUserId = rs.getInt(1);
                    }
                }

                // user_info INSERT
                try (PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
                    ps.setInt(1, newUserId);
                    ps.setString(2, info.getNickname());
                    if (info.getRegionId() == null) ps.setNull(3, Types.INTEGER);
                    else ps.setInt(3, info.getRegionId());
                    ps.setString(4, info.getAddrDetail());
                    ps.setString(5, info.getProfileImg());
                    ps.executeUpdate();
                }

                conn.commit();
                return newUserId;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean isAccountIdDuplicated(String accountId) throws SQLException {
        String sql = "SELECT 1 FROM user WHERE account_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isNicknameDuplicated(String nickname) throws SQLException {
        String sql = "SELECT 1 FROM user_info WHERE nickname = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public User login(String accountId, String pw) throws SQLException {
        String sql = """
            SELECT id, account_id, pw, name, phn, em, created_at
            FROM user
            WHERE account_id = ? AND pw = ?
            LIMIT 1
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ps.setString(2, pw); // 해시를 쓰면 여기 대신 해시 비교 로직으로

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                User u = new User();
                u.setId(rs.getInt("id"));
                u.setAccountId(rs.getString("account_id"));
                u.setPw(rs.getString("pw"));          // 세션엔 pw 안 넣는 걸 권장
                u.setName(rs.getString("name"));
                u.setPhn(rs.getString("phn"));
                u.setEm(rs.getString("em"));
                return u;
            }
        }
    }
    
    public UserProfile findProfileByUserId(int userId) throws SQLException {
        String sql = """
            SELECT u.id, u.account_id, u.name, u.phn, u.em,
                   i.nickname, i.addr_detail, i.profile_img
            FROM user u
            LEFT JOIN user_info i ON i.u_id = u.id
            WHERE u.id = ?
            LIMIT 1
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                UserProfile u = new UserProfile();
                u.setId(rs.getInt("id"));
                u.setAccountId(rs.getString("account_id"));
                u.setName(rs.getString("name"));
                u.setPhn(rs.getString("phn"));
                u.setEm(rs.getString("em"));
                u.setNickname(rs.getString("nickname"));
				/* int rid = rs.getInt("region_id"); */
				/* u.setRegionId(rs.wasNull() ? null : rid); */
                u.setAddrDetail(rs.getString("addr_detail"));
                u.setProfileImg(rs.getString("profile_img"));
                
                return u;
            }
            
        }

    }


}
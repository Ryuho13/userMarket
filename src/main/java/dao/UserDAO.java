package dao;

import java.sql.*;
import model.User;
import model.UserInfo;

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
}

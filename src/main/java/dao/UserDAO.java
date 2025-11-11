package dao;

import java.sql.*;
import model.User;
import model.UserInfo;
import model.UserProfile;

public class UserDAO {

    /* 회원가입 (User + UserInfo 저장) */
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
                    ps.setString(5, info.getProfileImg()); // 없으면 null
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

    /* 아이디 중복 체크 */
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

    /* 닉네임 중복 체크 */
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

    /* 로그인 */
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
            ps.setString(2, pw);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                User u = new User();
                u.setId(rs.getInt("id"));
                u.setAccountId(rs.getString("account_id"));
                u.setPw(rs.getString("pw"));
                u.setName(rs.getString("name"));
                u.setPhn(rs.getString("phn"));
                u.setEm(rs.getString("em"));
                return u;
            }
        }
    }

    /* 마이페이지용 프로필 조회 (시/도, 시/군/구 이름 포함) */
    public UserProfile findProfileByUserId(int userId) throws SQLException {
        String sql = """
            SELECT
                u.id            AS userId,
                u.account_id    AS accountId,
                u.name          AS name,
                u.em            AS em,
                u.phn           AS phn,
                ui.nickname     AS nickname,
                ui.region_id    AS regionId,
                ui.addr_detail  AS addrDetail,
                ui.profile_img  AS profileImg,
                sa.name         AS sido_name,
                ga.name         AS sigg_name
            FROM user u
            LEFT JOIN user_info ui   ON u.id = ui.u_id
            LEFT JOIN sigg_areas ga  ON ga.id = ui.region_id
            LEFT JOIN sido_areas sa  ON sa.id = ga.sido_area_id
            WHERE u.id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserProfile p = new UserProfile();
                    p.setUserId(rs.getInt("userId"));
                    p.setAccountId(rs.getString("accountId"));
                    p.setName(rs.getString("name"));
                    p.setEm(rs.getString("em"));
                    p.setPhn(rs.getString("phn"));
                    p.setNickname(rs.getString("nickname"));
                    p.setRegionId((Integer) rs.getObject("regionId"));
                    p.setAddrDetail(rs.getString("addrDetail"));
                    p.setProfileImg(rs.getString("profileImg"));

                    // ↓↓↓ 표시용 이름 세팅 (UserProfile에 필드/세터 추가 필요)
                    try { p.setSidoName(rs.getString("sido_name")); } catch (Throwable ignore) {}
                    try { p.setSiggName(rs.getString("sigg_name")); } catch (Throwable ignore) {}

                    return p;
                }
            }
        }
        return null;
    }

    public void updateUserAndInfo(
            int userId,
            String name,
            String phn,
            String emNullable,
            String newPwNullable,
            String nickname,
            String addrDetail,
            String profileImgNullable,
            Integer regionIdNullable   // ✅ 추가
    ) throws SQLException {

        String sqlUser = """
            UPDATE user
               SET name = ?,
                   phn  = ?,
                   em   = ?,
                   pw   = CASE WHEN ? IS NULL OR ? = '' THEN pw ELSE ? END
             WHERE id = ?
        """;

        // ✅ region_id도 업데이트 (null 가능)
        StringBuilder sqlInfoBuilder = new StringBuilder("""
            UPDATE user_info
               SET nickname    = ?,
                   addr_detail = ?,
                   region_id   = ?
        """);

        boolean updateProfileImg = (profileImgNullable != null && !profileImgNullable.isBlank());
        if (updateProfileImg) {
            sqlInfoBuilder.append(", profile_img = ?");
        }
        sqlInfoBuilder.append(" WHERE u_id = ?");
        String sqlInfo = sqlInfoBuilder.toString();

        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);

                // user
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setString(1, name);
                    ps.setString(2, phn);
                    if (emNullable == null || emNullable.isBlank()) ps.setNull(3, Types.VARCHAR);
                    else ps.setString(3, emNullable);

                    if (newPwNullable == null) {
                        ps.setNull(4, Types.VARCHAR);
                        ps.setNull(5, Types.VARCHAR);
                        ps.setNull(6, Types.VARCHAR);
                    } else {
                        ps.setString(4, newPwNullable);
                        ps.setString(5, newPwNullable);
                        ps.setString(6, newPwNullable);
                    }
                    ps.setInt(7, userId);
                    ps.executeUpdate();
                }

                // user_info
                try (PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
                    int i = 1;
                    ps.setString(i++, nickname);
                    ps.setString(i++, addrDetail);
                    if (regionIdNullable == null) ps.setNull(i++, Types.INTEGER);
                    else ps.setInt(i++, regionIdNullable);

                    if (updateProfileImg) {
                        ps.setString(i++, profileImgNullable);
                    }
                    ps.setInt(i++, userId);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /* 회원 탈퇴 */
    public boolean deleteUserById(int userId) throws SQLException {
        String sqlInfo = "DELETE FROM user_info WHERE u_id = ?";
        String sqlUser = "DELETE FROM user WHERE id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlInfo);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUser)) {
                ps1.setInt(1, userId);
                ps1.executeUpdate();

                ps2.setInt(1, userId);
                int affected = ps2.executeUpdate();

                conn.commit();
                return affected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}

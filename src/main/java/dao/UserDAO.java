package dao;

import java.sql.*;
import model.User;
import model.UserInfo;
import model.UserProfile;
import model.Product;
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
						if (!rs.next())
							throw new SQLException("No generated key for user");
						newUserId = rs.getInt(1);
					}
				}

				// user_info INSERT
				try (PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
					ps.setInt(1, newUserId);
					ps.setString(2, info.getNickname());
					if (info.getRegionId() == null)
						ps.setNull(3, Types.INTEGER);
					else
						ps.setInt(3, info.getRegionId());
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

	/* 아이디 중복 체크 */
	public boolean isAccountIdDuplicated(String accountId) throws SQLException {
		String sql = "SELECT 1 FROM user WHERE account_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, accountId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	/* 닉네임 중복 체크 */
	public boolean isNicknameDuplicated(String nickname) throws SQLException {
		String sql = "SELECT 1 FROM user_info WHERE nickname = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

		try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, accountId);
			ps.setString(2, pw);

			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next())
					return null;

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

	/* 마이페이지용 프로필 조회 */
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
				        ui.profile_img  AS profileImg
				    FROM user u
				    LEFT JOIN user_info ui ON u.id = ui.u_id
				    WHERE u.id = ?
				""";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
					return p;
				}
			}
		}
		return null;
	}
	
	/* 마이페이지 수정 */
	public void updateUserAndInfo(int userId, String name, String phn, String emNullable, String newPwNullable,
	        String nickname, String addrDetail, String profileImgNullable) throws SQLException {

	    // --- (sqlUser 쿼리 생략, 기존과 동일하게 유지) ---
	    String sqlUser = """
	            UPDATE user
	            SET name = ?,
	            phn  = ?,
	            em   = ?,
	            pw   = CASE WHEN ? IS NULL OR ? = '' THEN pw ELSE ? END
	            WHERE id = ?
	            """;

	    // ✅ 수정된 sqlInfo: 프로필 이미지가 NULL이 아닌 경우에만 업데이트하도록 동적 쿼리 구성
	    StringBuilder sqlInfoBuilder = new StringBuilder("""
	            UPDATE user_info
	            SET nickname = ?,
	            addr_detail = ?
	            """);

	    // profileImgNullable이 null이 아닐 경우에만 profile_img 컬럼을 업데이트 목록에 추가
	    boolean updateProfileImg = (profileImgNullable != null && !profileImgNullable.isBlank());
	    if (updateProfileImg) {
	        sqlInfoBuilder.append(", profile_img = ?");
	    }
	    
	    // 주소 (region_id) 업데이트 로직이 필요하다면 여기에 추가
	    
	    sqlInfoBuilder.append(" WHERE u_id = ?");
	    String sqlInfo = sqlInfoBuilder.toString();


	    try (Connection conn = DBUtil.getConnection()) {
	        try {
	            conn.setAutoCommit(false);

	            // 1. user 업데이트 (기존 로직 유지)
	            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
	                ps.setString(1, name);
	                ps.setString(2, phn);

	                if (emNullable == null || emNullable.isBlank())
	                    ps.setNull(3, Types.VARCHAR);
	                else
	                    ps.setString(3, emNullable);

	                // pw 업데이트
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

	            // 2. user_info 업데이트 (수정된 로직)
	            try (PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
	                int paramIndex = 1;

	                ps.setString(paramIndex++, nickname);
	                ps.setString(paramIndex++, addrDetail);
	                
	                // profileImg가 업데이트 대상일 경우에만 바인딩
	                if (updateProfileImg) {
	                    ps.setString(paramIndex++, profileImgNullable);
	                } else {
	                    // 이미지를 삭제하라는 요청이 있을 경우 (예: "delete" 플래그)는 
	                    // 별도 처리되어야 하지만, 현재는 '전송된 새로운 이미지가 없으면 유지'가 목표
	                }

	                ps.setInt(paramIndex++, userId);
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
	 private static String normalizeDisplayImg(String imgName) {
	        if (imgName == null || imgName.isEmpty()) {
	            return "/product/resources/images/noimage.jpg";
	        }
	        String display = imgName.replace("/userMarket", "");
	        if (!display.contains("/upload/product_images/")) {
	            display = "/upload/product_images/" + display;
	        }
	        return display;
	    }
	 
	    public Product getProductById(int id) throws SQLException {
	        String sql = """
	            SELECT p.id AS product_id, MIN(i.name) AS img_name
				FROM products p
				LEFT JOIN product_images pi ON p.id = pi.product_id
				LEFT JOIN images i ON pi.image_id = i.id
				WHERE p.id = ?
				GROUP BY p.id;

	        """;

	        try (Connection conn = DBUtil.getConnection();
	        	     PreparedStatement ps = conn.prepareStatement(sql)) {
	        	    ps.setInt(1, id);
	        	    try (ResultSet rs = ps.executeQuery()) {
	        	        if (rs.next()) {
	        	            String displayImg = normalizeDisplayImg(rs.getString("img_name"));
	        	            return new Product(
	        	                    rs.getInt("product_id"),
	        	                    displayImg
	        	            );
	        	        }
	        	    }
	        	}
	        	return null;

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

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>회원정보 수정</title>
</head>
<body>
	<%
	request.setCharacterEncoding("UTF-8");

	// 폼 파라미터
	String id = request.getParameter("id");
	String name = request.getParameter("name");
	String nickname = request.getParameter("nickname");
	String phone = request.getParameter("phone");
	String address = request.getParameter("address");

	String pw = request.getParameter("password"); // 비번: 비워두면 유지
	String pw2 = request.getParameter("password_confirm"); // (프론트에서 체크했겠지만 혹시 몰라 간단 확인)

	String mail1 = request.getParameter("mail1");
	String mail2 = request.getParameter("mail2");
	String email = (mail1 != null && mail2 != null && !mail1.isEmpty() && !mail2.isEmpty()) ? (mail1 + "@" + mail2) : null;

	String url = "jdbc:mysql://localhost:3306/usermarketdb";
	String user = "root";
	String pass = "test1234";

	Connection conn = null;
	PreparedStatement psUser = null;
	PreparedStatement psInfo = null;

	try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, user, pass);

		String sqlUser = "UPDATE `user` " + "   SET name = ?, phn = ?, em = ?, pw = IF(? = '' OR ? IS NULL, pw, ?) "
		+ " WHERE id = ?";

		psUser = conn.prepareStatement(sqlUser);
		psUser.setString(1, name);
		psUser.setString(2, phone);
		if (email != null)
			psUser.setString(3, email);
		else
			psUser.setNull(3, Types.VARCHAR);
		psUser.setString(4, pw); // IF 비교용
		psUser.setString(5, pw); // IS NULL 비교용
		psUser.setString(6, pw); // 실제 업데이트 값
		psUser.setString(7, id);

		int u1 = psUser.executeUpdate();

		// 2) user_info 테이블 업데이트
		String sqlInfo = "UPDATE user_info SET nickname = ?, addr_detail = ? WHERE id = ?";
		psInfo = conn.prepareStatement(sqlInfo);
		psInfo.setString(1, nickname);
		psInfo.setString(2, address);
		psInfo.setString(3, id);
		int u2 = psInfo.executeUpdate();

		response.sendRedirect("myPage.jsp");

	} catch (Exception e) {
		out.println("<p style='color:red'>오류: " + e.getMessage() + "</p>");
		e.printStackTrace();
	} finally {
		try {
			if (psInfo != null)
		psInfo.close();
		} catch (Exception ignore) {
		}
		try {
			if (psUser != null)
		psUser.close();
		} catch (Exception ignore) {
		}
		try {
			if (conn != null)
		conn.close();
		} catch (Exception ignore) {
		}
	}
	%>

</body>
</html>

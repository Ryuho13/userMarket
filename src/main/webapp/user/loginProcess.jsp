<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>로그인</title>

</head>
<body>
	<%@ page import="java.sql.*"%>
	<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String pw = request.getParameter("pw");

	try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/usermarketdb", "root", "test1234");

		PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM user WHERE account_id = ? AND pw = ?");
		ps.setString(1, id);
		ps.setString(2, pw);

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			session.setAttribute("loginAccountId", id);
			session.setAttribute("loginUserId", rs.getInt("id"));
			session.setAttribute("loginName", rs.getString("name"));

			response.sendRedirect(request.getContextPath() + "/user/myPage");
			return;
		} else {
	%>
	<script>
		alert("아이디 또는 비밀번호가 올바르지 않습니다.");
		history.back();
	</script>
	<%
	}
	rs.close();
	ps.close();
	conn.close();
	} catch (Exception e) {
	e.printStackTrace();
	}
	%>


</body>
</html>
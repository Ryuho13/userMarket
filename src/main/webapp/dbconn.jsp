<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Database SQL</title>
</head>
<body>
	<!-- JDBC API로 데이터베이스 접속하기 -->
	<%
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		try {
			String url = "jdbc:mysql://127.0.0.1:3306/bookmarketdb";
			String user = "root";
			String password = "a010203";
			
			// JDBC 드라이버 로딩
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			// Connection 객체 얻기(JDBC 드라이버 -> DB 연결)
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			out.println("SQLException" + e.getMessage());
		}
	%>
</body>
</html>
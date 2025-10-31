<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입 처리</title>
</head>
<body>
<%
    request.setCharacterEncoding("UTF-8");

    String id = request.getParameter("id");
    String pw = request.getParameter("pw");
    String name = request.getParameter("name");
    String nickname = request.getParameter("nickname"); 
    String phone = request.getParameter("phone");
    String address = request.getParameter("address"); 

    String mail1 = request.getParameter("mail1");
    String mail2 = request.getParameter("mail2");
    String email = (mail1 != null && mail2 != null) ? mail1 + "@" + mail2 : null;

    String dbURL = "jdbc:mysql://localhost:3306/usermarketdb";
    String dbUser = "root";
    String dbPass = "a010203";

    Connection conn = null;
    PreparedStatement psUser = null;
    PreparedStatement psInfo = null;
    ResultSet rs = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
        conn.setAutoCommit(false);

        String sqlUser = "INSERT INTO user (account_id, pw, name, phn, em, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
        psUser.setString(1, id);
        psUser.setString(2, pw);
        psUser.setString(3, name);
        psUser.setString(4, phone);
        psUser.setString(5, email);
        psUser.executeUpdate();

        rs = psUser.getGeneratedKeys();
        int userId = 0;
        if (rs.next()) userId = rs.getInt(1);

        String sqlInfo = "INSERT INTO user_info (u_id, nickname, addr_detail) VALUES (?, ?, ?)";
        psInfo = conn.prepareStatement(sqlInfo);
        psInfo.setInt(1, userId);
        psInfo.setString(2, nickname);
        psInfo.setString(3, address);
        psInfo.executeUpdate();

        conn.commit();
%>
        <script>
            location.href = "welcome.jsp";
        </script>
<%
    } catch (Exception e) {
        if (conn != null) try { conn.rollback(); } catch (Exception ignore) {}
%>
        <p style="color:red;">회원가입 중 오류가 발생했습니다:<br><%= e.getMessage() %></p>
<%
        e.printStackTrace();
    } finally {
        if (rs != null) try { rs.close(); } catch (Exception ignore) {}
        if (psUser != null) try { psUser.close(); } catch (Exception ignore) {}
        if (psInfo != null) try { psInfo.close(); } catch (Exception ignore) {}
        if (conn != null) try { conn.close(); } catch (Exception ignore) {}
    }
%>
</body>
</html>

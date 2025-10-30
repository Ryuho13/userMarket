<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품(테스트용)</title>
</head>
<body>
<%
    // 상품 ID 가져오기
    String idParam = request.getParameter("id");
    long productId = (idParam != null && !idParam.isEmpty()) ? Long.parseLong(idParam) : 1L; // 기본값 1

    // 로그인 미구현 -> 테스트용 사용자 ID 
    long userId = 999L; // 999번 사용자를 테스트 ID로 사용
%>

<h2>상품 상세 (테스트용)</h2>
<p>상품 ID: <%= productId %></p>
<p>구매자 ID(테스트용): <%= userId %></p>

<form action="<%= request.getContextPath() %>/chatRoom" method="post">
    <input type="hidden" name="productId" value="<%= productId %>">
    <input type="hidden" name="buyerId" value="<%= userId %>">
    <button type="submit">채팅하기</button>
</form>

</body>
</html>

<!-- /WEB-INF/views/signup_success.jsp -->
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>가입 성공</title></head>
<body>
  <h2>회원가입이 완료되었습니다!</h2>
  <p>회원번호: <strong><%= request.getAttribute("memberId") %></strong></p>
  <a href="<%= request.getContextPath() %>/login">로그인으로 이동</a>
</body>
</html>

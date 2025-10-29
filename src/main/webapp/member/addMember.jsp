<!-- /WEB-INF/views/signup.jsp -->
<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
</head>
<body>
	<h2>회원가입</h2>

	<%
	String error = (String) request.getAttribute("error");
	%>
	<%
	if (error != null) {
	%>
	<div style="color: red;"><%=error%></div>
	<%
	}
	%>

	<form method="post" action="<%=request.getContextPath()%>/member/signup.jsp">
		<label>이메일</label><br /> <input type="email" name="email" required /><br />
		<br /> <label>닉네임</label><br /> <input type="text" name="nickname"
			required minlength="2" maxlength="40" /><br />
		<br /> <label>비밀번호</label><br /> <input type="password"
			name="password" required minlength="4" /><br />
		<br /> <label>지역 코드(선택)</label><br /> <input type="text"
			name="regionCode" placeholder="SEOUL_GANGNAM" /><br />
		<br />

		<button type="submit">가입하기</button>
	</form>
</body>
</html>

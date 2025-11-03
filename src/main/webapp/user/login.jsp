<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>로그인 - 단감나라</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
<link rel="stylesheet" href="<%=request.getContextPath()%>/user/css/login.css">
</head>
<body>

	<h1 class="main-logo">
		<a href="${pageContext.request.contextPath}/product_list.jsp">
			<img alt="단감나라" src="<%=request.getContextPath()%>/user/img/real단감나라.png" style="width: 300px">
		</a>
	</h1>

	<div class="login-card-container">
		<div class="login-card">

			<h3 class="form-signin-heading text-center">로그인</h3>

			<div class="alert alert-danger" role="alert">아이디와 비밀번호를 확인해주세요.</div>

			<form class="form-signin" action="<%=request.getContextPath()%>/product/product_list.jsp" method="post">
				<div class="form-floating mb-3">
					<input type="text" class="form-control" name="id" id="floatingInput" placeholder="ID" required autofocus>
					<label for="floatingInput">아이디 (ID)</label>
				</div>
				<div class="form-floating mb-3">
					<input type="password" class="form-control" name="password" id="floatingPassword" placeholder="Password" required>
					<label for="floatingPassword">비밀번호 (Password)</label>
				</div>

				<button type="submit" class="btn btn-lg btn-success w-100 mt-2">로그인</button>

				<p class="mt-4 mb-0 text-center text-muted">
					계정이 없으신가요?<a href="addUser.jsp" class="text-decoration-none text-success"> 회원가입</a>
				</p>
			</form>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous">
	</script>
	<script
		src="<%=request.getContextPath()%>/user/js/login.js">
	</script>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>환영합니다!</title>
<script src="https://cdn.tailwindcss.com"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/user/css/welcome.css">
</head>
<body
	class="min-h-screen flex items-center justify-center p-4 bg-[#F0FFF4] font-['Inter']">
	<div class="max-w-xl mx-auto text-center p-8 sm:p-12 bg-white rounded-2xl shadow-lg overflow-hidden">

		<a href="${pageContext.request.contextPath}/index.jsp">
			<img alt="단감나라" src="<%=request.getContextPath()%>/user/img/real단감나라.png" class="block mx-auto mb-6" style="width: 300px">
		</a>
		<h1 class="welcome-title uppercase text-gray-800 tracking-tighter">
			환영합니다!
		</h1>

		<p class="subtitle uppercase text-green-600 mt-4">단감나라에 오신 것을 환영합니다!</p>

		<p class="text-gray-600 mt-8 mb-10 text-lg leading-relaxed">새로운 중고 상품을 판매하고 구매할 수 있습니다. 다양한 상품들을 만나보세요!</p>

		<a href="login.jsp"
			class="inline-block btn-custom-green text-white text-xl font-bold py-4 px-10 rounded-full shadow-lg hover:shadow-xl transition duration-300 transform hover:scale-105">
			로그인하러 하기
		</a>
	</div>
</body>
</html>

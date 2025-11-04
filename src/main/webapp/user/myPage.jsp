<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>마이페이지</title>
<script src="https://cdn.tailwindcss.com"></script>
<script src="https://unpkg.com/lucide@latest"></script>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
</head>
<body class="min-h-screen p-4 sm:p-8">
	<%-- <jsp:include page="/header/header.jsp" /> --%>
	<c:if test="${empty user}">
		<c:redirect url="${pageContext.request.contextPath}/user/myPage" />
	</c:if>

	<div class="max-w-4xl mx-auto bg-white shadow-xl rounded-2xl overflow-hidden">

		<!-- 헤더 및 제목 -->
		<header class="p-6 border-b border-gray-100">
			<h1 class="text-3xl font-bold text-gray-800 text-center">나의 마켓활동</h1>
		</header>

		<!-- 마이페이지 본문 레이아웃 -->
		<div class="flex flex-col md:flex-row p-6 md:space-x-8">

			<!-- 1. 좌측 사이드바: 프로필 정보 및 수정 버튼 -->
			<div class="md:w-1/3 space-y-6 mb-8 md:mb-0">

				<!-- 프로필 이미지 섹션 -->
				<div class="flex flex-col items-center">
					<!-- 이미지 컨테이너 -->
					<div id="profile-container"
						class="relative w-40 h-40 rounded-full overflow-hidden shadow-lg border-4 border-white ring-4 ring-green-100">
						<img id="profile-image"
							src="https://placehold.co/160x160/D1E7DD/1E9447?text=Profile"
							alt="프로필 이미지" class="w-full h-full object-cover">
					</div>

					<h2 id="user-nickname" class="text-2xl font-bold text-gray-800 mt-4"> ${user.nickname}</h2>
					<p id="user-region" class="text-gray-500 text-sm">${user.addrDetail}</p>
				</div>

				<!-- 프로필 수정 버튼 -->
				<a href="updateMyPage.jsp"
					class="block w-full text-center py-2 border border-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-50 transition duration-150">
					프로필 수정 (탈퇴 포함) </a>
			</div>

			<!-- 2. 우측 콘텐츠 영역: 목록 탭 -->
			<div class="md:w-2/3">
				<div class="border-b border-gray-200">
					<nav class="flex space-x-4 -mb-px">
						<!-- 등록상품 목록 탭 -->
						<button id="tab-products"
							class="tab-button border-b-2 font-medium py-2 px-1 text-green-500 border-green-500"
							onclick="changeTab('products')">등록상품 목록</button>
						<!-- 찜 목록 탭 -->
						<button id="tab-wishlist"
							class="tab-button border-b-2 font-medium py-2 px-1 text-gray-500 border-transparent hover:border-gray-300"
							onclick="changeTab('wishlist')">찜 목록</button>
						<!-- 채팅 목록 탭 -->
						<button id="tab-chats"
							class="tab-button border-b-2 font-medium py-2 px-1 text-gray-500 border-transparent hover:border-gray-300"
							onclick="changeTab('chats')">채팅 목록</button>
					</nav>
				</div>

				<div class="mt-4">
					<!-- 1. 등록상품 목록 -->
					<div id="content-products" class="tab-content">
						<div class="p-4 bg-gray-50 rounded-lg border border-gray-100">
							<p class="text-gray-600">등록된 상품이 없습니다.</p>
							<a href="#"
								class="text-green-500 font-semibold hover:underline mt-2 inline-block">상품
								등록하러 가기 &rarr;</a>
						</div>
					</div>

					<!-- 2. 찜 목록 -->
					<div id="content-wishlist" class="tab-content hidden">
						<div class="p-4 bg-gray-50 rounded-lg border border-gray-100">
							<p class="text-gray-600">찜한 상품이 없습니다.</p>
							<a href="#"
								class="text-green-500 font-semibold hover:underline mt-2 inline-block">인기
								상품 구경가기 &rarr;</a>
						</div>
					</div>

					<!-- 3. 채팅 목록 -->
					<div id="content-chats" class="tab-content hidden">
						<div class="space-y-3">
						<div class="p-4 bg-gray-50 rounded-lg border border-gray-100">
							<p class="text-gray-600">진행중인 채팅이 없습니다.</p>
						</div>
						</div>
					</div>
				</div>

			</div>
		</div>

	</div>

	<script src="${pageContext.request.contextPath}/user/js/myPage.js"></script>
	
</body>
</html>

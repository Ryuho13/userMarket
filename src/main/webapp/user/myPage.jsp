<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<%-- Sticky Footer 패턴 적용: body에서 전역 패딩(p-4 sm:p-8)을 제거하여 헤더/푸터가 가장자리에 붙게 합니다. --%>
<body class="flex flex-col min-h-screen">
	<jsp:include page="/header/header.jsp" />

	<%-- 중앙 컨테이너 max-w-6xl로 확장하고, flex-grow로 남은 공간을 채웁니다.
	    패딩(p-4 sm:p-8)은 main 태그에 적용합니다. --%>
	<main class="flex-grow p-4 sm:p-8">
		<div class="max-w-6xl mx-auto bg-white shadow-xl rounded-2xl overflow-hidden">

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

						<h2 id="user-nickname" class="text-2xl font-bold text-gray-800 mt-4">${profile.nickname}</h2>
						<p id="user-region" class="text-gray-500 text-sm break-words whitespace-pre-line"> ${profile.addrDetail}</p>
					</div>

					<!-- 프로필 수정, 탈퇴 버튼 -->
					<div class="space-y-3">
					  <a href="${pageContext.request.contextPath}/user/mypage/update"
						 class="block w-full text-center py-2 font-semibold rounded-lg 
								border border-green-500 text-green-600 hover:bg-green-50 
								transition duration-150 shadow-sm">
						프로필 수정
					  </a>
					  <a href="${pageContext.request.contextPath}/user/delete"
						 class="block w-full text-center py-2 font-semibold rounded-lg 
								border border-red-400 text-red-500 hover:bg-red-50 
								transition duration-150 shadow-sm" onclick="confirmDelete(event)">
						회원 탈퇴
					  </a>
					</div>
				</div>
				
				<script>
					// 이벤트 객체를 매개변수로 명시적으로 받도록 수정
					// 경고: alert/confirm 대신 커스텀 모달 UI를 사용해야 합니다.
					function confirmDelete(event) {
						event.preventDefault();
						if (confirm("정말 탈퇴하시겠습니까?\n\n회원님의 모든 거래 기록과 정보가 완전히 삭제됩니다.")) {
							window.location.href = event.currentTarget.getAttribute("href");
						}
					}
				</script>

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
								<a href="${pageContext.request.contextPath}/product/product_form"
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
								<c:choose>
									<c:when test="${not empty chatRooms}">
										<c:forEach var="chatRoom" items="${chatRooms}">
											<a href="${pageContext.request.contextPath}/chatRoom?roomId=${chatRoom.id}&currentUserId=${sessionScope.loginUserId}"
											   class="block p-4 bg-white rounded-lg border border-gray-200 hover:bg-gray-50 transition duration-150">
												<div class="flex justify-between items-center">
													<div>
														<p class="font-bold text-gray-800">
															<c:if test="${not empty chatRoom.otherUserNickname}">${chatRoom.otherUserNickname}님과의 채팅</c:if>
															<c:if test="${empty chatRoom.otherUserNickname}">알 수 없는 사용자와의 채팅</c:if>
														</p>
														<p class="text-sm text-gray-600">
															<c:if test="${not empty chatRoom.productTitle}">상품: ${chatRoom.productTitle}</c:if>
															<c:if test="${empty chatRoom.productTitle}">상품 정보 없음</c:if>
														</p>
													</div>
													<div class="text-right">
														<span class="text-xs text-gray-400">
															<c:if test="${not empty chatRoom.createdAt}">${chatRoom.createdAt}</c:if>
															<c:if test="${empty chatRoom.createdAt}">날짜 정보 없음</c:if>
														</span>
													</div>
												</div>
											</a>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<div class="p-4 bg-gray-50 rounded-lg border border-gray-100">
											<p class="text-gray-600">진행중인 채팅이 없습니다.</p>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>
	</main>
	<%-- 푸터 포함 --%>
	<script src="${pageContext.request.contextPath}/user/js/myPage.js"></script>
	<jsp:include page="/footer/footer.jsp" />
</body>
</html>
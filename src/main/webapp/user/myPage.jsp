<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>

<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>마이페이지</title>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script src="https://cdn.tailwindcss.com"></script>
<script>

</script>
<style>
/* 폰트 적용 및 기본 스타일 /
body {
font-family: 'Inter', sans-serif;
}
/ 사용자 정의 커스텀 모달 스타일 */
.custom-modal-overlay {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 1000;
	opacity: 0;
	visibility: hidden;
	transition: opacity 0.3s, visibility 0.3s;
}

.custom-modal-overlay.open {
	opacity: 1;
	visibility: visible;
}

.custom-modal-content {
	background: white;
	padding: 24px;
	border-radius: 12px;
	width: 90%;
	max-width: 400px;
	box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
	transform: scale(0.9);
	transition: transform 0.3s;
}

.custom-modal-overlay.open .custom-modal-content {
	transform: scale(1);
}
</style>
<script src="https://unpkg.com/lucide@latest"></script>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
</head>

<% request.setAttribute("now", System.currentTimeMillis()); %>


<body class="flex flex-col min-h-screen bg-gray-50">
	<%-- jsp:include는 그대로 유지 --%>
	<jsp:include page="/header/header.jsp" />

	<%-- Custom Modal for Confirmation (Replacing alert/confirm) --%>

	<div id="delete-confirm-modal" class="custom-modal-overlay"
		onclick="closeCustomModal()">
		<div class="custom-modal-content" onclick="event.stopPropagation()">
			<h3 class="text-xl font-bold text-red-600 mb-4">회원 탈퇴 확인</h3>
			<p class="text-gray-700 mb-6">정말 탈퇴하시겠습니까?</p>
			<p class="text-sm text-gray-500 mb-6">회원님의 모든 거래 기록과 정보가 완전히
				삭제됩니다.</p>
			<div class="flex justify-end space-x-3">
				<button onclick="closeCustomModal()"
					class="px-4 py-2 text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition duration-150">
					취소</button>
				<button id="confirm-delete-button"
					class="px-4 py-2 text-white bg-red-500 rounded-lg hover:bg-red-600 transition duration-150 font-semibold">
					탈퇴하기</button>
			</div>
		</div>
	</div>

	<main class="flex-grow p-4 sm:p-8">
		<div
			class="max-w-6xl mx-auto bg-white shadow-xl rounded-2xl overflow-hidden">

			<!-- 헤더 및 제목 -->

			<header class="p-6 border-b border-gray-100">
				<h1 class="text-3xl font-bold text-gray-800 text-center">나의
					마켓활동</h1>
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
								src="${pageContext.request.contextPath}/profile_images/user_${sessionScope.loginUser.id}.jpg?ts=${now}"
								onerror="(function(img){
                    var base='${pageContext.request.contextPath}/profile_images/user_${sessionScope.loginUser.id}';
                    if(!img.dataset.tried){
                      img.dataset.tried='png';
                      img.src=base+'.png?ts=${now}';
                    } else {
                      img.onerror=null;
                      img.src='https://placehold.co/128x128/D1E7DD/1E9447?text=Profile';
                    }
                  })(this)"
								alt="프로필 이미지" class="w-full h-full object-cover">
						</div>

						<h2 id="user-nickname"
							class="text-2xl font-bold text-gray-800 mt-4">${profile.nickname}</h2>
						<p id="user-region"
							class="text-gray-500 text-sm break-words whitespace-pre-line">
							${profile.addrDetail}</p>
					</div>

					<!-- 프로필 수정, 탈퇴 버튼 -->
					<div class="space-y-3">
						<a href="${pageContext.request.contextPath}/user/mypage/update"
							class="block w-full text-center py-2 font-semibold rounded-lg 
							border border-green-500 text-green-600 hover:bg-green-50 
							transition duration-150 shadow-sm">
							프로필 수정 </a> <a href="${pageContext.request.contextPath}/user/delete"
							id="delete-link"
							class="block w-full text-center py-2 font-semibold rounded-lg 
							border border-red-400 text-red-500 hover:bg-red-50 
							transition duration-150 shadow-sm">
							회원 탈퇴 </a>
					</div>
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
							<c:choose>
								<c:when test="${not empty products}">
									<div class="space-y-4">
										<c:forEach var="product" items="${products}">

											<%-- 전체 항목을 감싸던 <a> 태그를 제거하고, 대신 <div>를 사용합니다. --%>
											<div
												class="block p-4 bg-white rounded-xl border border-gray-200 shadow-sm flex space-x-4">

												<!-- 상품 썸네일 (✅ 여기만 링크로 만듭니다) -->
												<a
													href="${pageContext.request.contextPath}/product/detail?id=${product.id}"
													class="flex-shrink-0 w-24 h-24 rounded-lg overflow-hidden relative hover:opacity-75 transition duration-200">

													<%-- ✅ 이미지 로딩 태그 복구: 서블릿에서 설정된 URL 사용 --%> <img
													src="${ctx}${product.displayImg}"
													class="card-img-top product_img" alt="상품 이미지"
													onerror="this.src='${ctx}/product/resources/images/noimage.jpg'">

													<!-- 상태 뱃지 -->
													<div
														class="absolute bottom-1 right-1 px-2 py-0.5 text-xs font-semibold rounded-full
								<c:choose>
									<c:when test="${product.status eq 'SELLING'}">bg-green-500 text-white</c:when>
									<c:when test="${product.status eq 'RESERVED'}">bg-yellow-500 text-gray-900</c:when>
									<c:when test="${product.status eq 'SOLD'}">bg-gray-500 text-white</c:when>
									<c:otherwise>bg-gray-300 text-gray-700</c:otherwise>
								</c:choose>
							">
														<c:choose>
															<c:when test="${product.status eq 'SELLING'}">판매중</c:when>
															<c:when test="${product.status eq 'RESERVED'}">예약중</c:when>
															<c:when test="${product.status eq 'SOLD'}">거래완료</c:when>
															<c:otherwise>상태확인</c:otherwise>
														</c:choose>
													</div>
												</a>

												<!-- 상품 정보 (✅ 이제 클릭 불가능합니다) -->
												<div class="flex-grow min-w-0">
													<h3 class="text-2xl font-bold text-gray-800 truncate">${product.title}</h3>

													<%-- 가격 --%>
													<p class="text-base text-gray-600 mt-2">
														가격:
														<c:choose>
															<c:when
																test="${product.sellPrice == null || product.sellPrice == 0}">
										0원
									</c:when>
															<c:otherwise>
																<fmt:formatNumber value="${product.sellPrice}"
																	pattern="#,###" groupingUsed="true" />원
									</c:otherwise>
														</c:choose>
													</p>

													<%-- 등록일 --%>
												</div>
											</div>
										</c:forEach>
									</div>
								</c:when>
								<c:otherwise>
									<div
										class="p-6 bg-gray-50 rounded-xl border border-gray-100 shadow-inner text-center">
										<p class="text-gray-600 text-lg mb-4">아직 등록된 상품이 없습니다.</p>
										<a
											href="${pageContext.request.contextPath}/product/product_form"
											class="inline-flex items-center text-green-600 font-bold hover:underline transition duration-150">
											상품 등록하러 가기 &nbsp;<i data-lucide="arrow-right"></i>
										</a>
									</div>
								</c:otherwise>
							</c:choose>
						</div>

						<!-- 2. 찜 목록 -->
						<div id="content-wishlist" class="tab-content hidden">
							<div class="p-4 bg-gray-50 rounded-lg border border-gray-100">
								<p class="text-gray-600">찜한 상품이 없습니다.</p>
								<a href="${pageContext.request.contextPath}/product/list"
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
						          
						          <!-- 카드 전체 -->
						          <div class="block p-4 bg-white rounded-lg border border-gray-200 hover:bg-gray-50 transition duration-150">
						
						            <!-- 상단: 채팅 제목 + 상품명 + 날짜 (전체 클릭 시 채팅방으로 이동) -->
						            <a
						              href="${pageContext.request.contextPath}/chatRoom?roomId=${chatRoom.id}&currentUserId=${sessionScope.loginUserId}"
						              class="flex justify-between items-center">
						              
						              <div>
						                <p class="font-bold text-gray-800">
						                  <c:if test="${not empty chatRoom.otherUserNickname}">
						                    ${chatRoom.otherUserNickname}님과의 채팅
						                  </c:if>
						                  <c:if test="${empty chatRoom.otherUserNickname}">
						                    알 수 없는 사용자와의 채팅
						                  </c:if>
						                </p>
						                <p class="text-sm text-gray-600">
						                  <c:if test="${not empty chatRoom.productTitle}">
						                    상품: ${chatRoom.productTitle}
						                  </c:if>
						                  <c:if test="${empty chatRoom.productTitle}">
						                    상품 정보 없음
						                  </c:if>
						                </p>
						              </div>
						
						              <div class="text-right">
						                <span class="text-xs text-gray-400">
						                  <c:if test="${not empty chatRoom.createdAt}">${chatRoom.createdAt}</c:if>
						                  <c:if test="${empty chatRoom.createdAt}">날짜 정보 없음</c:if>
						                </span>
						              </div>
						            </a>
						
						            <!-- 하단: 버튼 영역 -->
						            <div class="mt-3 flex justify-end gap-2">
						
						              <%-- 1) 내가 판매자인 경우: 거래 완료 버튼 --%>
						              <c:if test="${chatRoom.sellerId == sessionScope.loginUserId}">
						                <form action="${pageContext.request.contextPath}/product/complete"
						                      method="post"
						                      onclick="event.stopPropagation()">
						
						                  <input type="hidden" name="productId" value="${chatRoom.productId}" />
						                  <input type="hidden" name="roomId" value="${chatRoom.id}" />
						
						                  <c:choose>
						                    <c:when test="${chatRoom.productStatus == 'SOLD_OUT'}">
						                      <button type="button"
						                              class="px-5 py-2 text-sm font-semibold rounded-full bg-gray-200 text-gray-500 cursor-not-allowed"
						                              disabled>
						                        거래 완료됨
						                      </button>
						                    </c:when>
						                    <c:otherwise>
						                      <button type="submit"
						                              class="px-5 py-2 text-sm font-semibold rounded-full bg-emerald-500 text-white hover:bg-emerald-600 transition">
						                        거래 완료
						                      </button>
						                    </c:otherwise>
						                  </c:choose>
						                </form>
						              </c:if>
						
						              <%-- 2) 내가 구매자이고, 상품이 이미 판매 완료된 경우: 판매자 평점 버튼 --%>
						              <c:if test="${chatRoom.buyerId == sessionScope.loginUserId
						                           && chatRoom.productStatus == 'SOLD_OUT'}">
						
						                <c:choose>
						                  <%-- 이미 평점 남겼으면: 비활성 --%>
						                  <c:when test="${chatRoom.rated}">
						                    <button type="button"
						                            class="px-5 py-2 text-sm font-semibold rounded-full bg-gray-200 text-gray-500 cursor-not-allowed"
						                            onclick="event.stopPropagation()"
						                            disabled>
						                      평가 완료
						                    </button>
						                  </c:when>
						
						                  <%-- 아직 평점 없으면: 평점 남기기 --%>
						                  <c:otherwise>
						                    <form action="${pageContext.request.contextPath}/rating/form"
						                          method="get"
						                          onclick="event.stopPropagation()">
						                      <input type="hidden" name="productId" value="${chatRoom.productId}" />
						                      <button type="submit"
						                              class="px-5 py-2 text-sm font-semibold rounded-full bg-blue-500 text-white hover:bg-blue-600 transition">
						                        판매자 평점 남기기
						                      </button>
						                    </form>
						                  </c:otherwise>
						                </c:choose>
						
						              </c:if>
						
						            </div>
						          </div>
						
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
	</main>
	<%-- 푸터 포함 --%>
	<script src="${pageContext.request.contextPath}/user/js/myPage.js"></script>
	<jsp:include page="/footer/footer.jsp" />
	<jsp:include page="../resources/alarm.jsp" />
	<script
		src="${pageContext.request.contextPath}/user/js/image-preview.js"></script>

</body>
</html>
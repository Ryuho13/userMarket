
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원 가입</title>
<script src="https://cdn.tailwindcss.com"></script>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/user/css/addUser.css">

</head>
<body class="min-h-screen flex items-start justify-center p-4">

	<div class="w-full login-card sm:p-10">
		<!-- 페이지 제목 -->
		<h1
			class="text-3xl font-extrabold text-gray-800 mb-6 border-b-2 form-signin-border pb-3 text-center form-signin-heading">
			중고 마켓 회원 가입</h1>

		<!-- 에러 메시지 표시 영역 -->
		<div id="error-message"
			class="hidden items-center justify-between p-3 mb-4 text-sm font-medium text-red-800 border border-red-300 rounded-lg bg-red-50"
			role="alert">
			<span class="font-semibold"></span>
			<button type="button"
				onclick="document.getElementById('error-message').classList.add('hidden')"
				class="ml-auto -mx-1.5 -my-1.5 bg-red-50 text-red-500 rounded-lg focus:ring-2 focus:ring-red-400 p-1.5 hover:bg-red-200 inline-flex items-center justify-center h-8 w-8">
				<svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"
					xmlns="http://www.w3.org/2000/svg">
	               <path fill-rule="evenodd"
						d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
						clip-rule="evenodd"></path></svg>
			</button>
		</div>

		<!-- 본문 영역 -->
		<form name="newUser"
			action="${pageContext.request.contextPath}/user/add" method="post"
			onsubmit="return checkAddForm(event)">

			<!-- 아이디 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">아이디</label>
				<div class="w-full md:w-3/4">
					<input name="id" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="영문, 숫자 6~12자" required value="${id}">
				</div>
			</div>

			<!-- 비밀번호 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">비밀번호</label>
				<div class="w-full md:w-3/4">
					<input name="pw" type="password"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="비밀번호" required>
				</div>
			</div>

			<!-- 비밀번호 확인 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">비밀번호
					확인</label>
				<div class="w-full md:w-3/4">
					<input name="password_confirm" type="password"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="비밀번호 확인" required>
				</div>
			</div>

			<!-- 성명 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">성명</label>
				<div class="w-full md:w-3/4">
					<input name="name" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="이름" required value="${name}">
				</div>
			</div>

			<!-- 닉네임 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">닉네임</label>
				<div class="w-full md:w-3/4">
					<input name="nickname" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="마켓에서 사용할 닉네임" required value="${nickname}">
				</div>
			</div>

			<!-- 이메일 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">이메일</label>
				<div class="w-full md:w-3/4">
					<div class="flex items-center space-x-2">
						<!-- 이메일 ID -->
						<input type="text" name="mail1" maxlength="50"
							class="w-1/3 p-2 border border-gray-300 rounded-lg input-field"
							placeholder="이메일 ID"> <span class="text-gray-600">@</span>
						<!-- 이메일 도메인 선택 -->
						<select name="mail2"
							class="w-2/3 p-2 border border-gray-300 rounded-lg input-field">
							<option value="naver.com">naver.com</option>
							<option value="daum.net">daum.net</option>
							<option value="gmail.com">gmail.com</option>
							<option value="nate.com">nate.com</option>
							<option value="">직접 입력</option>
						</select>
					</div>
				</div>
			</div>

			<!-- 전화번호 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">전화번호</label>
				<div class="w-full md:w-3/4">
					<input name="phn" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="'-' 없이 숫자만 입력" required>
				</div>
			</div>

			<!-- 주소 -->
			<div class="mb-6 flex flex-col md:flex-row">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700 pt-2">주소</label>

				<div class="w-full md:w-3/4 space-y-3">
					<!-- 시/도 + 시/군/구 선택 -->
					<div class="flex space-x-2">
						<!-- 시/도 -->
						<select id="sido" name="sidoId"
							class="form-select px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-1/2"
							required>
							<option value="">도/시 선택</option>
							<c:forEach var="sido" items="${sidoList}">
								<option value="${sido.id}"
									<c:if test="${sido.id == sidoId}">selected</c:if>>
									${sido.name}</option>
							</c:forEach>
						</select>

						<!-- 시/군/구 -->
						<select id="sigg" name="regionId"
							class="form-select px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-1/2"
							required>
							<option value="">시/군/구 선택</option>
							<c:if test="${not empty siggList}">
								<c:forEach var="sigg" items="${siggList}">
									<option value="${sigg.id}"
										<c:if test="${sigg.id == regionId}">selected</c:if>>
										${sigg.name}</option>
								</c:forEach>
							</c:if>
						</select>
					</div>

					<!-- 상세 주소 -->
					<input name="addr3" type="text"
						class="w-full p-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
						placeholder="도로명, 지번, 건물명 등 상세 주소" value="${addr3}" required>
				</div>
			</div>

			<!-- 버튼 영역 -->
			<div
				class="flex flex-col sm:flex-row justify-end space-y-3 sm:space-y-0 sm:space-x-3 pt-4 border-t border-gray-200">
				<button type="submit"
					class="primary-green px-6 py-2 text-white font-bold rounded-lg shadow-md hover:shadow-lg transition duration-150 focus:outline-none focus-ring w-full sm:w-auto">
					가입하기</button>
				<button type="reset"
					class="bg-gray-200 text-gray-700 px-6 py-2 font-bold rounded-lg shadow-md hover:bg-gray-300 transition duration-150 focus:outline-none focus:ring-4 focus:ring-gray-300 w-full sm:w-auto">
					초기화</button>
				<button type="back" onclick="history.back()"
					class="bg-gray-200 text-gray-700 px-6 py-2 font-bold rounded-lg shadow-md hover:bg-gray-300 transition duration-150 focus:outline-none focus:ring-4 focus:ring-gray-300 w-full sm:w-auto">
					뒤로가기</button>
			</div>
		</form>
	</div>
	<script>window.APP_CTX = '<%=request.getContextPath()%>';</script>
	<script src="<%=request.getContextPath()%>/user/js/addUser.js"></script>


</body>
</html>

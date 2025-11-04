<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원 가입</title>
<!-- Tailwind CSS CDN -->
<script src="https://cdn.tailwindcss.com"></script>

<!-- 외부 CSS 파일 참조: /user/css/addUser.css -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/user/css/addUser.css">

</head>
<!-- body 클래스: 커스텀 CSS에서 배경색 처리하므로, min-h-screen flex items-start justify-center p-4 만 유지 -->
<body class="min-h-screen flex items-start justify-center p-4">

	<!-- div 클래스: login-card (커스텀 CSS) 만 유지하고 나머지는 Tailwind의 반응형 스타일만 유지 -->
	<div class="w-full login-card sm:p-10">
		<!-- 페이지 제목 -->
		<!-- Tailwind 색상/경계선 클래스 제거, 커스텀 CSS 클래스 적용 -->
		<h1
			class="text-3xl font-extrabold text-gray-800 mb-6 border-b-2 form-signin-border pb-3 text-center form-signin-heading">
			중고 마켓 회원 가입</h1>

		<!-- 에러 메시지 표시 영역 (Tailwind 유틸리티 클래스 유지) -->
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
		<form name="newUser" action="processAddUser.jsp" method="post"
			onsubmit="return checkAddForm(event)">

			<!-- 아이디 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">아이디</label>
				<div class="w-full md:w-3/4">
					<!-- focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
					<input name="id" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="영문, 숫자 6~12자" required>
				</div>
			</div>

			<!-- 비밀번호 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">비밀번호</label>
				<div class="w-full md:w-3/4">
					<!-- focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
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
					<!-- focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
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
					<!-- focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
					<input name="name" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="이름" required>
				</div>
			</div>

			<!-- 닉네임 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">닉네임</label>
				<div class="w-full md:w-3/4">
					<!-- focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
					<input name="nickname" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="마켓에서 사용할 닉네임" required>
				</div>
			</div>

			<!-- 이메일 -->
			<div class="mb-4 flex flex-col md:flex-row md:items-center">
				<label
					class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700">이메일</label>
				<div class="w-full md:w-3/4">
					<div class="flex items-center space-x-2">
						<!-- 이메일 ID: focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
						<input type="text" name="mail1" maxlength="50"
							class="w-1/3 p-2 border border-gray-300 rounded-lg input-field"
							placeholder="이메일 ID"> <span class="text-gray-600">@</span>
						<!-- 이메일 도메인 선택: focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
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
					<!-- focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
					<input name="phone" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="'-' 없이 숫자만 입력" required>
				</div>
			</div>

			<!-- 주소 -->
			<div class="mb-6 flex flex-col md:flex-row">
				<label class="w-full md:w-1/4 mb-1 md:mb-0 font-medium text-gray-700 pt-2">주소</label>
				<div class="w-full md:w-3/4 space-y-3">
					
					<!-- 1. 도/시 선택: focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
					<div class="flex space-x-2">
						<select name="addr1" id="addr1-select"
							class="w-1/2 p-2 border border-gray-300 rounded-lg input-field"
							required>
							<option value="">도/시 선택</option>
							<option value="서울특별시">서울특별시</option>
							<option value="부산광역시">부산광역시</option>
							<option value="대구광역시">대구광역시</option>
							<option value="인천광역시">인천광역시</option>
							<option value="광주광역시">광주광역시</option>
							<option value="대전광역시">대전광역시</option>
							<option value="울산광역시">울산광역시</option>
							<option value="세종특별자치시">세종특별자치시</option>
							<option value="경기도">경기도</option>
							<option value="강원특별자치도">강원특별자치도</option>
							<option value="충청북도">충청북도</option>
							<option value="충청남도">충청남도</option>
							<option value="전라북도">전라북도</option>
							<option value="전라남도">전라남도</option>
							<option value="경상북도">경상북도</option>
							<option value="경상남도">경상남도</option>
							<option value="제주특별자치도">제주특별자치도</option>
						</select>
						
						<!-- 2. 시/군/구 선택: focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
						<select name="addr2" id="addr2-select"
							class="w-1/2 p-2 border border-gray-300 rounded-lg input-field"
							required>
							<option value="">시/군/구 선택</option>
						</select>
					</div>

					<!-- 3. 상세 주소 입력: focus:border-[#1E9447] 제거, input-field 클래스 추가 -->
					<input name="addr3" type="text"
						class="w-full p-2 border border-gray-300 rounded-lg input-field"
						placeholder="도로명, 지번, 건물명 등 상세 주소" required>
						
				</div>
			</div>

			<!-- 버튼 영역 -->
			<div
				class="flex flex-col sm:flex-row justify-end space-y-3 sm:space-y-0 sm:space-x-3 pt-4 border-t border-gray-200">
				<!-- 등록 버튼: Tailwind 색상/호버/포커스 클래스 제거, 커스텀 CSS 클래스 적용 -->
				<button type="submit"
					class="primary-green px-6 py-2 text-white font-bold rounded-lg shadow-md hover:shadow-lg transition duration-150 focus:outline-none focus-ring w-full sm:w-auto">
					가입하기</button>
				<!-- 취소 버튼 (Secondary: Tailwind 클래스 유지) -->
				<button type="reset"
					class="bg-gray-200 text-gray-700 px-6 py-2 font-bold rounded-lg shadow-md hover:bg-gray-300 transition duration-150 focus:outline-none focus:ring-4 focus:ring-gray-300 w-full sm:w-auto">
					초기화</button>
			</div>
		</form>
	</div>

	<!-- 외부 JavaScript 파일 참조: /user/js/addUser.js -->
	<script src="<%=request.getContextPath()%>/user/js/addUser.js"></script>
    
</body>
</html>

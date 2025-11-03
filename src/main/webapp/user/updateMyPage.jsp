<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>회원 정보 수정</title>

<script src="https://cdn.tailwindcss.com"></script>
<script src="https://unpkg.com/lucide@latest"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/user/css/updateMyPage.css">

<style type="text/css">
body {
	font-family: 'Inter', sans-serif;
	background-color: #f3f5f7;
}

.primary-green {
	background-color: #1E9447;
	border-color: #1E9447;
}

.primary-green:hover {
	background-color: #177a39;
	border-color: #177a39;
}

.text-green-500 {
	color: #1E9447;
}
</style>

</head>
<body class="min-h-screen p-4 sm:p-8">
	<div class="max-w-xl mx-auto bg-white p-6 sm:p-8 shadow-xl rounded-2xl">

		<!-- 페이지 제목 -->
		<h1 class="text-3xl font-bold text-gray-800 text-center mb-8 text-green-500">
			회원 정보 수정</h1>

		<!-- 커스텀 에러 메시지 표시 영역 -->
		<div id="error-message"
			class="hidden bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative mb-4"
			role="alert">
			<span class="block sm:inline" id="error-text"></span>
		</div>

		<!-- 프로필 이미지 섹션 (마이페이지 디자인 재활용) -->
		<div class="flex flex-col items-center mb-10">
			<input type="file" id="profile-upload" class="hidden"
				accept="image/*" onchange="previewImage(event)">

			<!-- 이미지 컨테이너 (클릭 가능) -->
			<div id="profile-container"
				class="relative w-32 h-32 cursor-pointer rounded-full overflow-hidden shadow-lg border-4 border-white ring-4 ring-green-100"
				onclick="document.getElementById('profile-upload').click()">
				<!-- DB에서 로드할 현재 프로필 이미지 URL. (수정 시 기본값) -->
				<img id="profile-image"
					src="https://placehold.co/128x128/D1E7DD/1E9447?text=Profile"
					alt="프로필 이미지" class="w-full h-full object-cover">

				<!-- 카메라 아이콘 (업로드 표시) -->
				<div
					class="absolute bottom-0 right-0 p-1 bg-white rounded-full shadow-md border border-gray-200">
					<i data-lucide="camera" class="w-4 h-4 text-gray-600"></i>
				</div>
			</div>
		</div>

		<!-- 본문 영역 -->
		<form name="updateMember" action="processUpdateMember.jsp"
			method="post" onsubmit="return checkForm(event)" class="space-y-4">

			<!-- 아이디 (수정 불가 필드 - 순서 변경 없음) -->
			<div class="space-y-1">
				<label for="id" class="text-sm font-medium text-gray-700 block">아이디
					(ID)</label>
				<!-- DB에서 로드할 사용자 ID. 수정 불가 (readonly) -->
				<input name="id" type="text"
					class="form-input w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100 text-gray-500 cursor-not-allowed"
					value="user_id_123" readonly>
			</div>

			<!-- 1. 성명 (Name) - 순서 변경됨 -->
			<div class="space-y-1">
				<label for="name" class="text-sm font-medium text-gray-700 block">성명
					(Name)</label>
				<!-- DB에서 로드할 사용자 이름 -->
				<input name="name" type="text"
					class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
					placeholder="성명" value="홍길동" required>
			</div>

			<!-- 2. 닉네임 (Nickname) - 순서 변경됨 -->
			<div class="space-y-1">
				<label for="nickname"
					class="text-sm font-medium text-gray-700 block">닉네임
					(Nickname)</label>
				<!-- DB에서 로드할 사용자 닉네임 -->
				<input name="nickname" type="text"
					class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
					placeholder="닉네임" value="마켓_고수" required>
			</div>

			<!-- 3. 비밀번호 (Password) - 순서 변경됨 -->
			<div class="space-y-1">
				<label for="password"
					class="text-sm font-medium text-gray-700 block">비밀번호
					(Password)</label> <input name="password" type="password"
					class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
					placeholder="새 비밀번호를 입력하거나 현재 비밀번호 유지" value="">
			</div>

			<!-- 4. 비밀번호 확인 (Password Confirm) - 순서 변경됨 -->
			<div class="space-y-1">
				<label for="password_confirm"
					class="text-sm font-medium text-gray-700 block">비밀번호 확인</label> <input
					name="password_confirm" type="password"
					class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
					placeholder="비밀번호 재확인" value="">
			</div>

<!-- 			<!-- 생일 (Birth) - 순서 변경 없음 
			<div class="space-y-1">
				<label class="text-sm font-medium text-gray-700 block mb-1">생일
					(Birth)</label>
				DB에서 로드한 생일 데이터를 기반으로 기본값 설정
				<div class="flex space-x-2">
					<input type="text" name="birthyy" maxlength="4"
						class="form-input px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-1/3"
						placeholder="년(4자)" value="1990"> <select name="birthmm"
						class="form-select px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-1/3">
						<option value="">월</option>
						<option value="01">1</option>
						<option value="02">2</option>
						<option value="03" selected>3</option>
						예시로 3월 선택
						<option value="04">4</option>
						<option value="05">5</option>
						<option value="06">6</option>
						<option value="07">7</option>
						<option value="08">8</option>
						<option value="09">9</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
					</select> <input type="text" name="birthdd" maxlength="2"
						class="form-input px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-1/3"
						placeholder="일" value="15">
				</div>
			</div> -->

			<!-- 이메일 (Email) - 순서 변경 없음 -->
			<div class="space-y-1">
				<label class="text-sm font-medium text-gray-700 block mb-1">이메일
					(Email)</label>
				<!-- DB에서 로드한 이메일 데이터를 기반으로 기본값 설정 -->
				<div class="flex space-x-2 items-center">
					<input type="text" name="mail1" maxlength="50"
						class="form-input px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-full md:w-5/12"
						placeholder="email" value="usermail"> <span
						class="text-gray-500">@</span> <select name="mail2"
						class="form-select px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-full md:w-5/12">
						<option value="naver.com">naver.com</option>
						<option value="daum.net" selected>daum.net</option>
						<!-- 예시로 daum.net 선택 -->
						<option value="gmail.com">gmail.com</option>
						<option value="nate.com">nate.com</option>
					</select>
				</div>
			</div>

			<!-- 전화번호 (Phone) - 순서 변경 없음 -->
			<div class="space-y-1">
				<label for="phone" class="text-sm font-medium text-gray-700 block">전화번호
					(Phone)</label>
				<!-- DB에서 로드할 전화번호 -->
				<input name="phone" type="text"
					class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
					placeholder="'-' 없이 입력" value="01012345678">
			</div>

			<!-- 주소 (Address) - 순서 변경 없음 -->
			<div class="space-y-1">
				<label for="address" class="text-sm font-medium text-gray-700 block">주소
					(Address)</label>
				<!-- DB에서 로드할 주소 -->
				<input name="address" type="text"
					class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
					placeholder="주소" value="서울시 강남구 역삼동">
			</div>

			<!-- 버튼 영역 -->
			<div class="pt-4 flex justify-between space-x-4">
				<button type="submit"
					class="w-full py-2 primary-green text-white font-semibold rounded-lg shadow-md transition duration-150">
					정보 수정 완료</button>
				<button type="reset"
					class="w-full py-2 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition duration-150">
					수정 취소</button>
			</div>
		</form>
	</div>
	<script
		src="<%=request.getContextPath()%>/user/js/updateMyPage.js">
	</script>
</body>
</html>

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
</head>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%
	request.setAttribute("now", System.currentTimeMillis());
%>

<body class="min-h-screen p-4 sm:p-8">
  <div class="max-w-xl mx-auto bg-white p-6 sm:p-8 shadow-xl rounded-2xl">

    <!-- 페이지 제목 -->
    <h1 class="text-3xl font-bold text-gray-800 text-center mb-8 text-green-500">
      회원 정보 수정
    </h1>

    <!-- 커스텀 에러 메시지 표시 영역 -->
    <div id="error-message"
         class="hidden bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative mb-4"
         role="alert">
      <span class="block sm:inline" id="error-text"></span>
    </div>

    <!-- 본문 영역 -->
    <form name="updateMyPage"
          action="${pageContext.request.contextPath}/user/mypage/update"
          method="post"
          onsubmit="return checkForm(event)"
          class="space-y-4"
          enctype="multipart/form-data">

      <!-- 프로필 이미지 섹션 -->
      <div class="flex flex-col items-center mb-6">
        <!-- 파일 인풋 (숨김) -->
        <input type="file" id="profile-upload" name="profile-upload"
               class="hidden" accept="image/*" onchange="previewImage(event)">

        <!-- 이미지 컨테이너 (클릭 시 파일 선택) -->
        <div id="profile-container"
             class="relative w-32 h-32 cursor-pointer rounded-full overflow-hidden shadow-lg border-4 border-white ring-4 ring-green-100"
             onclick="document.getElementById('profile-upload').click()">

          <!-- DB 없이 userId 기반 고정 파일명으로 로드 + 캐시 무력화 -->
          <img id="profile-image"
          		 src="${ctx}/profile_images/user_${sessionScope.loginUser.id}.jpg?ts=${now}"
     					 onerror="(function(img){var base='${ctx}/profile_images/user_${sessionScope.loginUser.id}';
       									if(!img.dataset.tried){ img.dataset.tried='png'; img.src=base+'.png?ts=${now}'; }
       									else { img.onerror=null; img.src='https://placehold.co/128x128/D1E7DD/1E9447?text=Profile'; }})(this)"
     					 alt="프로필 이미지" class="w-full h-full object-cover">

          <!-- 카메라 아이콘 -->
          <div class="absolute bottom-0 right-0 p-1 bg-white rounded-full shadow-md border border-gray-200">
            <i data-lucide="camera" class="w-4 h-4 text-gray-600"></i>
          </div>
        </div>
        <div class="flex items-center gap-2 mt-4">
				  <input id="removeProfile" type="checkbox" name="removeProfile" value="Y"
				         class="w-4 h-4 border-gray-300 rounded">
				  <label for="removeProfile" class="text-sm text-gray-700">프로필 이미지 삭제</label>
				</div>
      </div>

      <!-- 아이디 (수정 불가) -->
      <div class="space-y-1">
        <label for="id" class="text-sm font-medium text-gray-700 block">아이디 (ID)</label>
        <input name="id" type="text"
               class="form-input w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100 text-gray-500 cursor-not-allowed"
               value="${profile.accountId}" readonly>
      </div>

      <!-- 성명 -->
      <div class="space-y-1">
        <label for="name" class="text-sm font-medium text-gray-700 block">성명 (Name)</label>
        <input name="name" type="text"
               class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
               placeholder="성명" value="${profile.name}" required>
      </div>

      <!-- 닉네임 -->
      <div class="space-y-1">
        <label for="nickname" class="text-sm font-medium text-gray-700 block">닉네임 (Nickname)</label>
        <input name="nickname" type="text"
               class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
               placeholder="닉네임" value="${profile.nickname}" required>
      </div>

      <!-- 비밀번호 -->
      <div class="space-y-1">
        <label for="password" class="text-sm font-medium text-gray-700 block">비밀번호 (Password)</label>
        <input name="password" type="password"
               class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
               placeholder="새 비밀번호를 입력하거나 현재 비밀번호 유지" value="">
      </div>

      <!-- 비밀번호 확인 -->
      <div class="space-y-1">
        <label for="password_confirm" class="text-sm font-medium text-gray-700 block">비밀번호 확인</label>
        <input name="password_confirm" type="password"
               class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
               placeholder="비밀번호 재확인" value="">
      </div>

      <!-- 이메일 -->
      <div class="space-y-1">
        <label class="text-sm font-medium text-gray-700 block mb-1">이메일 (Email)</label>
        <div class="flex space-x-2 items-center">
          <input type="text" name="mail1" value="${mail1}" maxlength="50"
                 class="form-input px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-full md:w-5/12"
                 placeholder="email">
          <span class="text-gray-500">@</span>
          <select name="mail2"
                  class="form-select px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-full md:w-5/12">
            <option value="naver.com" <c:if test="${mail2 eq 'naver.com'}">selected</c:if>>naver.com</option>
            <option value="daum.net"  <c:if test="${mail2 eq 'daum.net'}">selected</c:if>>daum.net</option>
            <option value="gmail.com" <c:if test="${mail2 eq 'gmail.com'}">selected</c:if>>gmail.com</option>
            <option value="nate.com"  <c:if test="${mail2 eq 'nate.com'}">selected</c:if>>nate.com</option>
            <option value="">직접입력</option>
          </select>
        </div>
      </div>

      <!-- 전화번호 -->
      <div class="space-y-1">
        <label for="phone" class="text-sm font-medium text-gray-700 block">전화번호 (Phone)</label>
        <input name="phone" type="text"
               class="form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
               placeholder="'-' 없이 입력" value="${profile.phn}">
      </div>

      <!-- 주소 -->
      <div class="space-y-1">
        <label class="text-sm font-medium text-gray-700 block mb-1">주소 (Address)</label>
        <div class="w-full md:w-3/4 space-y-3">
          <div class="flex space-x-2">
            <select id="sido" name="sidoId"
                    class="form-select px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-1/2"
                    required>
              <option value="">도/시 선택</option>
              <c:forEach var="sido" items="${sidoList}">
                <option value="${sido.id}" <c:if test="${sido.id == sidoId}">selected</c:if>>${sido.name}</option>
              </c:forEach>
            </select>

            <select id="sigg" name="regionId"
                    class="form-select px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 w-1/2"
                    required>
              <option value="">시/군/구 선택</option>
              <c:if test="${not empty siggList}">
                <c:forEach var="sigg" items="${siggList}">
                  <option value="${sigg.id}" <c:if test="${sigg.id == regionId}">selected</c:if>>${sigg.name}</option>
                </c:forEach>
              </c:if>
            </select>
          </div>

          <input name="addr3" type="text"
                 class="w-full p-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500"
                 placeholder="도로명, 지번, 건물명 등 상세 주소" value="${addr3}" required>
        </div>
      </div>

      <!-- 버튼 영역 -->
      <div class="pt-4 flex justify-between space-x-4">
        <button type="submit"
                class="w-full py-2 primary-green text-white font-semibold rounded-lg shadow-md transition duration-150">
          정보 수정 완료
        </button>
        <button type="button"
                onclick="location.href='${pageContext.request.contextPath}/user/myPage'"
                class="w-full py-2 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition duration-150">
          수정 취소
        </button>
      </div>
    </form>
  </div>

  <script src="<%=request.getContextPath()%>/user/js/updateMyPage.js"></script>
</body>
</html>

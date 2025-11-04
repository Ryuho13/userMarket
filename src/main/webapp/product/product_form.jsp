<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>상품 등록</title>
<script src="https://cdn.tailwindcss.com"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

</head>

<body class="bg-gray-50 min-h-screen py-10">
  <div class="max-w-3xl mx-auto bg-white rounded-2xl shadow-lg overflow-hidden">
    
    <!-- 헤더 -->
    <div class="bg-green-500 text-white text-center py-6">
      <h1 class="text-2xl font-bold">상품 등록 / 수정</h1>
      <p class="text-sm opacity-80">당신의 물건을 다른 사람과 나눠보세요!</p>
    </div>

    <div class="p-8">
      <form id="productForm" method="post" enctype="multipart/form-data"
      action="${pageContext.request.contextPath}/product/insert"
      class="space-y-6">

  <!-- 상품명 -->
  <div>
    <label for="title" class="form-label fw-bold text-gray-700">상품명</label>
    <input type="text" class="form-control" id="title" name="title"
           required value="${product.title}">
  </div>

  <!-- 설명 -->
  <div>
    <label for="description" class="form-label fw-bold text-gray-700">설명</label>
    <textarea class="form-control" id="description" name="description"
              rows="5" placeholder="상품에 대한 설명을 입력하세요...">${product.description}</textarea>
  </div>

  <!-- 가격 / 상태 / 지역 -->
  <div class="row g-3">
    <div class="col-md-4">
      <label for="sellPrice" class="form-label fw-bold text-gray-700">가격 (원)</label>
      <input type="number" class="form-control" id="sellPrice" name="sellPrice"
             required value="${product.sellPrice}">
    </div>
    <div class="col-md-4">
      <label for="status" class="form-label fw-bold text-gray-700">상태</label>
      <select class="form-select" id="status" name="status">
        <option value="판매중" ${product.status == '판매중' ? 'selected' : ''}>판매중</option>
        <option value="예약중" ${product.status == '예약중' ? 'selected' : ''}>예약중</option>
        <option value="거래완료" ${product.status == '거래완료' ? 'selected' : ''}>거래완료</option>
      </select>
    </div>
    <div class="row g-3">
  <!-- 시도 -->
  <div class="col-md-6">
    <label for="sido" class="form-label fw-bold text-gray-700">시/도</label>
    <select id="sido" class="form-select">
      <option value="">선택</option>
      <c:forEach var="sido" items="${sidoList}">
        <option value="${sido.id}">${sido.name}</option>
      </c:forEach>
    </select>
  </div>

  <!-- 시군구 -->
  <div class="col-md-6">
    <label for="sigg" class="form-label fw-bold text-gray-700">시/군/구</label>
    <select id="sigg" class="form-select" name="siggId">
      <option value="">선택</option>
    </select>
  </div>
</div>
  <!-- ✅ 카테고리 -->
  <div>
    <label for="categoryId" class="form-label fw-bold text-gray-700">카테고리</label>
    <select class="form-select" id="categoryId" name="categoryId" required>
      <option value="">카테고리 선택</option>
      <c:forEach var="cat" items="${categoryList}">
        <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>
          ${cat.name}
        </option>
      </c:forEach>
    </select>
  </div>

  <!-- 이미지 업로드 -->
  <div>
    <label for="images" class="form-label fw-bold text-gray-700">이미지 업로드</label>
    <input class="form-control" type="file" id="images" name="images" accept="image/*" multiple>
    <c:if test="${not empty product.imgSrc}">
      <div class="mt-3 border p-3 rounded-lg bg-gray-50">
        <p class="text-sm text-gray-600 mb-2">현재 이미지</p>
        <img src="${product.imgSrc}" alt="product" class="rounded-lg shadow-md max-w-xs">
      </div>
    </c:if>
  </div>

  <!-- 버튼 -->
  <div class="flex justify-end gap-3 mt-6">
    <button type="submit" class="btn btn-success px-4 py-2">저장</button>
    <a href="${pageContext.request.contextPath}/product/list"
       class="btn btn-outline-secondary px-4 py-2">취소</a>
  </div>
</form>

    </div>
  </div>
  <script>
  const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/area-select.js"></script>
  
<script src="${pageContext.request.contextPath}/user/js/area-select.js"></script>
  
</body>
</html>

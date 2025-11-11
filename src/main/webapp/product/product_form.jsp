<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>
  <c:choose>
    <c:when test="${empty product.id}">
      ${sessionScope.loginUser.name}님의 상품 등록
    </c:when>
    <c:otherwise>
      ${sessionScope.loginUser.name}님의 상품 수정
    </c:otherwise>
  </c:choose>
</title>

<script src="https://cdn.tailwindcss.com"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-gray-50 min-h-screen py-10">

  <div class="max-w-3xl mx-auto bg-white rounded-2xl shadow-lg overflow-hidden">
    
    <!-- 헤더 -->
    <div class="bg-green-500 text-white text-center py-6">
      <h1 class="text-2xl font-bold">
        ${sessionScope.loginUser.name}님의 
        <c:choose>
          <c:when test="${empty product.id}">
            상품 등록
          </c:when>
          <c:otherwise>
            상품 수정
          </c:otherwise>
        </c:choose>
      </h1>
      <p class="text-sm opacity-80">당신의 물건을 다른 사람과 나눠보세요!</p>
    </div>

    <div class="p-8">
      <form id="productForm" method="post" enctype="multipart/form-data"
            action="${pageContext.request.contextPath}/product/${empty product.id ? 'insert' : 'update'}"
            class="space-y-6">
		
		  <input type="hidden" name="id" value="${product.id}">
		
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

        <!-- 가격 -->
        <div class="row g-3">
          <div class="col-md-6">
            <label for="sellPrice" class="form-label fw-bold text-gray-700">가격 (원)</label>
            <input type="number" class="form-control" id="sellPrice" name="sellPrice"
                   required value="${product.sellPrice}">
          </div>

          <!-- 상태 -->
          <div class="col-md-6">
            <c:choose>
              <c:when test="${empty product.id}">
                <!-- 등록 시 판매중 고정 -->
                <input type="hidden" name="status" value="SALE">
              </c:when>
              <c:otherwise>
                <!-- 수정 시만 상태 선택 -->
                <label for="status" class="form-label fw-bold text-gray-700">상태</label>
                <select class="form-select" id="status" name="status">
                  <option value="SALE" ${product.status == 'SALE' ? 'selected' : ''}>판매중</option>
                  <option value="RESERVED" ${product.status == 'RESERVED' ? 'selected' : ''}>예약중</option>
                  <option value="SOLD_OUT" ${product.status == 'SOLD_OUT' ? 'selected' : ''}>거래완료</option>
                </select>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <!-- 지역 선택 -->
        <div class="row g-3">
          <div class="col-md-6">
            <label for="sido" class="form-label fw-bold text-gray-700">시/도</label>
            <select id="sido" name="sidoId" class="form-select">
              <option value="">선택</option>
              <c:forEach var="sido" items="${sidoList}">
                <option value="${sido.id}" ${sido.id == product.sidoId ? 'selected' : ''}>${sido.name}</option>
              </c:forEach>
            </select>
          </div>

          <div class="col-md-6">
            <label for="sigg" class="form-label fw-bold text-gray-700">시/군/구</label>
            <select id="sigg" name="regionId" class="form-select">
              <option value="">선택</option>
              <c:if test="${not empty siggList}">
                <c:forEach var="sigg" items="${siggList}">
                  <option value="${sigg.id}" ${sigg.id == product.regionId ? 'selected' : ''}>${sigg.name}</option>
                </c:forEach>
              </c:if>
            </select>
          </div>
        </div>

        <!-- 카테고리 -->
        <div>
          <label for="categoryId" class="form-label fw-bold text-gray-700">카테고리</label>
          <select class="form-select" id="categoryId" name="categoryId" required>
            <option value="">카테고리 선택</option>
            <c:forEach var="cat" items="${categoryList}">
              <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
            </c:forEach>
          </select>
        </div>

            <!-- 이미지 업로드 -->
    <div>
      <label for="images" class="form-label fw-bold text-gray-700">이미지 업로드</label>
      <input class="form-control" type="file" id="images" name="images" accept="image/*" multiple>

      <!-- 현재 이미지 -->
      <c:if test="${not empty product.imgSrc}">
        <div class="mt-3 border p-3 rounded-lg bg-gray-50">
          <p class="text-sm text-gray-600 mb-2">현재 이미지</p>
          <img src="${product.imgSrc}" alt="product" class="rounded-lg shadow-md max-w-xs">
        </div>
      </c:if>

      <!-- 🔹 새 이미지 미리보기 -->
      <div id="previewContainer" class="mt-3 flex flex-wrap gap-3"></div>
    </div>

    <!-- 🔴 여기서 등록/수정 form을 닫아줌!! -->
  </form>

  <!-- 🔵 이제 여기부터는 등록/수정 폼 밖이므로, 삭제용 폼이 완전히 분리됨 -->
  <c:if test="${not empty product.id}">
 	<div class="flex justify-content-between">
   </c:if>

    <!-- 삭제 버튼: 상품이 있을 때만 -->
    <c:if test="${not empty product.id}">
      <form method="post"
            action="${pageContext.request.contextPath}/product/delete"
            onsubmit="return confirm('정말로 이 상품과 관련된 모든 데이터를 삭제하시겠습니까?');">
        <input type="hidden" name="id" value="${product.id}">
        <button type="submit" class="btn btn-danger px-4 py-2">삭제</button>
      </form>
    </c:if>

    <!-- 수정 / 목록으로 -->
    <div class="flex justify-end gap-3">
      <button type="submit" form="productForm"
              class="btn btn-success px-4 py-2">
        <c:choose>
          <c:when test="${empty product.id}">등록</c:when>
          <c:otherwise>수정</c:otherwise>
        </c:choose>
      </button>

      <a href="${pageContext.request.contextPath}/product/list"
         class="btn btn-outline-secondary px-4 py-2">목록</a>
    </div>
  </div>

<script>
  const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/user/js/area-select.js"></script>
<script src="${pageContext.request.contextPath}/user/js/image-preview.js"></script>

</body>
</html>

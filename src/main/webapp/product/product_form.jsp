<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
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
<link rel="stylesheet" href="<c:url value='/product/css/product_form.css'/>">
</head>

<body class="bg-gray-50 min-h-screen py-10">
  <div class="max-w-3xl mx-auto bg-white rounded-2xl shadow-lg overflow-hidden">
    <div class="bg-green-500 text-white text-center py-6">
      <h1 class="text-2xl font-bold">
        ${sessionScope.loginUser.name}님의 
        <c:choose>
          <c:when test="${empty product.id}">상품 등록</c:when>
          <c:otherwise>상품 수정</c:otherwise>
        </c:choose>
      </h1>
      <p class="text-sm opacity-80">당신의 물건을 다른 사람과 나눠보세요!</p>
    </div>

    <div class="p-8">
      <form id="productForm" method="post" enctype="multipart/form-data"
            action="${pageContext.request.contextPath}/product/${empty product.id ? 'insert' : 'update'}"
            class="space-y-6">

        <input type="hidden" name="id" value="${product.id}">

        <div>
          <label for="title" class="form-label fw-bold text-gray-700">상품명</label>
          <input type="text" class="form-control" id="title" name="title"
                 required value="${product.title}">
        </div>

        <div>
          <label for="description" class="form-label fw-bold text-gray-700">설명</label>
          <textarea class="form-control" id="description" name="description"
                    rows="5">${product.description}</textarea>
        </div>

        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label fw-bold text-gray-700">가격 (원)</label>
            <input type="number" class="form-control" name="sellPrice"
                   required value="${product.sellPrice}">
          </div>

          <div class="col-md-6">
            <c:choose>
              <c:when test="${empty product.id}">
                <input type="hidden" name="status" value="SALE">
              </c:when>
              <c:otherwise>
                <label class="form-label fw-bold text-gray-700">상태</label>
                <select class="form-select" name="status">
                  <option value="SALE" ${product.status == 'SALE' ? 'selected' : ''}>판매중</option>
                  <option value="RESERVED" ${product.status == 'RESERVED' ? 'selected' : ''}>예약중</option>
                  <option value="SOLD_OUT" ${product.status == 'SOLD_OUT' ? 'selected' : ''}>거래완료</option>
                </select>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label fw-bold text-gray-700">시/도</label>
            <select id="sido" name="sidoId" class="form-select">
              <option value="">선택</option>
              <c:forEach var="sido" items="${sidoList}">
                <option value="${sido.id}" ${sido.id == product.sidoId ? 'selected' : ''}>${sido.name}</option>
              </c:forEach>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label fw-bold text-gray-700">시/군/구</label>
            <select id="sigg" name="regionId" class="form-select">
              <option value="">선택</option>
              <c:forEach var="sigg" items="${siggList}">
                <option value="${sigg.id}" ${sigg.id == product.regionId ? 'selected' : ''}>${sigg.name}</option>
              </c:forEach>
            </select>
          </div>
        </div>

        <div>
          <label class="form-label fw-bold text-gray-700">카테고리</label>
          <select class="form-select" name="categoryId">
            <option value="">카테고리 선택</option>
            <c:forEach var="cat" items="${categoryList}">
              <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
            </c:forEach>
          </select>
        </div>

        <div>
          <label class="form-label fw-bold text-gray-700">이미지 업로드</label>
          <input class="form-control" type="file" id="images" name="images" accept="image/*" multiple>

          <div class="mt-3 border p-3 rounded-lg bg-gray-50">
            <p class="text-sm text-gray-600 mb-2">이미지 목록</p>

            <div class="flex flex-wrap gap-3">
              <c:forEach var="img" items="${productImages}">
                <div class="relative inline-block current-image" id="img-${img}">
                  <c:choose>
                    <c:when test="${fn:startsWith(img,'http')}">
                      <c:set var="src" value="${img}" />
                    </c:when>
                    <c:otherwise>
                      <c:set var="src" value="${ctx}/upload/product_images/${img}" />
                    </c:otherwise>
                  </c:choose>

                  <img src="${src}"
                       style="width:128px;height:128px;object-fit:cover"
                       class="rounded-lg shadow-md">

                  <button type="button"
                          class="delete-image-btn"
                          data-img="${img}"
                          data-product="${product.id}"
                          style="
                            position:absolute;
                            top:-8px; right:-8px;
                            width:24px; height:24px;
                            background:#ff4d4d; color:white;
                            border:none; border-radius:50%;
                          ">✕</button>

                  <input type="hidden" name="oldImages" value="${img}">
                </div>
              </c:forEach>

              <div id="previewContainer"></div>
            </div>
          </div>
        </div>

        <div class="flex justify-between mt-6 items-center">
          
          <c:choose>
            <c:when test="${not empty product.id}">
              <button type="button"
                      onclick="confirmDelete()"
                      class="btn-custom-outline btn-custom-red rounded-lg px-4 py-2">
                삭제하기
              </button>
            </c:when>
            <c:otherwise>
              <div class="w-0"></div> 
            </c:otherwise>
          </c:choose>
          <div class="flex gap-3">
            <button type="submit" class="btn btn-success px-4 py-2 rounded-lg">
              <c:choose>
                <c:when test="${empty product.id}">등록하기</c:when>
                <c:otherwise>수정하기</c:otherwise>
              </c:choose>
            </button>
            <a onclick="history.back()"
               class="btn-custom-outline btn-custom-gray rounded-lg px-4 py-2">뒤로가기</a>
          </div>
        </div>
      </form>
      <!-- 삭제 폼 -->
      <c:if test="${not empty product.id}">
        <form id="deleteForm"
              action="${ctx}/product/delete"
              method="post"
              style="display:none;">
          <input type="hidden" name="id" value="${product.id}">
        </form>
      </c:if>
    </div>
  </div>
<script>
  const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${ctx}/product/js/area-select.js"></script>
<script src="${ctx}/product/js/image-preview.js"></script>
<script src="${ctx}/product/js/delete.js"></script>
</body>
</html>

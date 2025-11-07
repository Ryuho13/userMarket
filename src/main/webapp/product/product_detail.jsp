<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>${product.title}</title>

<!-- 부트스트랩 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="<c:url value='/user/css/product_detail.css'/>">

<style>
  body { background-color: #f8f9fa; }
  .product-container { display: flex; gap: 3rem; align-items: flex-start; }
  .carousel-item img {
    width: 100%;
    border-radius: 8px;
    object-fit: cover;
    height: 400px;
  }
  .seller-box {
    background: #fff;
    border-radius: 8px;
    padding: 1.2rem 1.5rem;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  }
  .btn-action {
    min-width: 120px;
  }
  .btn-disabled {
    pointer-events: none;
    opacity: 0.6;
  }
</style>
</head>

<body class="py-5">

<div class="container">

  <a href="${pageContext.request.contextPath}/product/list" class="btn btn-outline-secondary mb-4">← 목록으로</a>

  <div class="product-container">

    <!-- 🔹 이미지 캐러셀 -->
    <div id="productCarousel" class="carousel slide col-5" data-bs-ride="carousel">
      <div class="carousel-inner">
        <c:forEach var="img" items="${product.images}" varStatus="status">
          <div class="carousel-item ${status.first ? 'active' : ''}">
            <img src="${img}" class="d-block w-100" alt="상품 이미지">
          </div>
        </c:forEach>
      </div>
      <button class="carousel-control-prev" type="button" data-bs-target="#productCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon"></span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#productCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon"></span>
      </button>
    </div>

    <!-- 🔹 상품 정보 -->
    <div class="product-info flex-grow-1">
      <div class="d-flex align-items-center justify-content-between">
        <h3 class="fw-bold mb-0">
          ${product.title}
          <!-- 판매 상태 뱃지 -->
          <c:choose>
            <c:when test="${product.status eq 'SALE'}">
              <span class="badge bg-success ms-2">판매중</span>
            </c:when>
            <c:when test="${product.status eq 'RESERVED'}">
              <span class="badge bg-warning text-dark ms-2">예약중</span>
            </c:when>
          </c:choose>
        </h3>

        <!-- 내 상품이면 수정 버튼 표시 -->
        <c:if test="${sessionScope.loginUserId == product.sellerId}">
          <a href="${pageContext.request.contextPath}/product/update?id=${product.id}"
             class="btn btn-outline-primary btn-sm">수정하기</a>
        </c:if>
      </div>

      <p class="text-muted mt-1 mb-3">지역: ${product.sellerSigg}</p>
      <h4 class="text-danger fw-bold mb-3">${product.sellPrice}원</h4>
      <p>${product.description}</p>
      <hr>

      <!-- 🔹 판매자 정보 -->
      <div class="seller-box mt-4">
        <h6 class="fw-bold mb-2">판매자 정보</h6>
        <p class="mb-1">연락처: <strong>${product.sellerMobile}</strong></p>
        <p class="mb-2">평점: <c:out value="${product.sellerRating != null ? product.sellerRating : '-'}" /></p>

        <div class="d-flex gap-2 mt-3">
          <c:choose>
            <c:when test="${not empty sessionScope.loginUserId}">
              <a href="${pageContext.request.contextPath}/chat/?sellerId=${product.sellerId}"
                 class="btn btn-primary btn-action
                        ${product.status eq 'SOLD_OUT' ? 'btn-disabled' : ''}">
                채팅하기
              </a>
              <button class="btn btn-outline-secondary btn-action
                             ${product.status eq 'SOLD_OUT' ? 'btn-disabled' : ''}">
                찜
              </button>
            </c:when>

            <c:otherwise>
              <a href="${pageContext.request.contextPath}/user/login.jsp" class="btn btn-outline-primary btn-action">
                로그인 후 채팅하기
              </a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>

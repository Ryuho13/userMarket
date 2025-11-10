<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>${product.title}</title>

<!-- Bootstrap -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="<c:url value='/user/css/product_detail.css'/>">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
</head>

<body class="py-5">
<div class="container">

  <a href="${pageContext.request.contextPath}/product/list" class="btn btn-outline-secondary mb-4">← 목록으로</a>

  <div class="product-container d-flex gap-4 flex-wrap">

    <!-- 🔹 이미지 캐러셀 -->
    <div id="productCarousel" class="carousel slide col-12 col-md-5" data-bs-ride="carousel" data-bs-interval="2500">
      <div class="carousel-inner">
        <c:forEach var="img" items="${product.images}" varStatus="status">
          <div class="carousel-item ${status.first ? 'active' : ''}">
            <img src="${img}" class="d-block mx-auto" alt="상품 이미지">
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
          <c:choose>
            <c:when test="${product.status eq 'SALE'}">
              <span class="badge bg-success ms-2">판매중</span>
            </c:when>
            <c:when test="${product.status eq 'RESERVED'}">
              <span class="badge bg-warning text-dark ms-2">예약중</span>
            </c:when>
            <c:when test="${product.status eq 'SOLD_OUT'}">
              <span class="badge bg-secondary ms-2">판매완료</span>
            </c:when>
          </c:choose>
        </h3>

        <c:if test="${sessionScope.loginUserId == product.sellerId}">
          <a href="${pageContext.request.contextPath}/product/update?id=${product.id}" class="btn btn-outline-primary btn-sm">수정하기</a>
        </c:if>
      </div>

		<p class="text-muted mt-1 mb-3">
		  지역:
		  <c:choose>
		    <c:when test="${not empty product.sidoName}">
		      ${product.sidoName}
		      <c:if test="${not empty product.regionName}">
		        ${product.regionName}
		      </c:if>
		    </c:when>
		    <c:otherwise>등록된 지역 없음</c:otherwise>
		  </c:choose>
		</p>

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
              <a href="${pageContext.request.contextPath}/chatRoom?productId=${product.id}&buyerId=${sessionScope.loginUserId}" 
                 class="btn btn-primary btn-action ${product.status eq 'SOLD_OUT' ? 'disabled' : ''}">
                채팅하기
              </a>
              <c:choose>
				  <c:when test="${sessionScope.loginUserId != null}">
				    <button 
					  id="wishBtn"
					  class="btn btn-outline-secondary btn-action"
					  data-product-id="${product.id}"
					  data-wish="${isWished}">
					  <i class="bi ${isWished ? 'bi-heart-fill text-danger' : 'bi-heart'}"></i> 찜
					</button>

				  </c:when>
				  <c:otherwise>
				    <a href="${pageContext.request.contextPath}/user/login?redirect=/product/detail?id=${product.id}"
				       class="btn btn-outline-secondary btn-action">
				      찜하려면 로그인
				    </a>
				  </c:otherwise>
				</c:choose>

            </c:when>
            <c:otherwise>
			  <a href="${pageContext.request.contextPath}/user/login.jsp?redirect=${pageContext.request.requestURI}?id=${product.id}"
			     class="btn btn-outline-primary btn-action">
			    로그인 후 채팅하기
			  </a>
			</c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>

  <!-- 🔸 비슷한 카테고리 상품 -->
  <c:if test="${not empty sameCategory}">
    <div class="section-box">
      <h5 class="section-title"><i class="bi bi-box-seam"></i> 비슷한 카테고리의 상품</h5>
      <div class="row row-cols-1 row-cols-sm-2 row-cols-md-4 g-3" id="categoryProducts">
        <c:forEach var="item" items="${sameCategory}" varStatus="status">
          <div class="col product-item ${status.index >= 4 ? 'd-none extra-category' : ''}">
            <a href="${pageContext.request.contextPath}/product/detail?id=${item.id}" class="text-decoration-none text-dark">
              <div class="card h-100 border-0 shadow-sm">
                <img src="${pageContext.request.contextPath}${item.displayImg}" class="card-img-top" alt="${item.title}">
                <div class="card-body">
                  <h6 class="card-title text-truncate mb-1">${item.title}</h6>
                  <small class="text-muted d-block mb-1">${item.siggName}</small>
                  <p class="card-text text-danger fw-bold mb-0">${item.sellPrice}원</p>
                </div>
              </div>
            </a>
          </div>
        </c:forEach>
      </div>
      <c:if test="${fn:length(sameCategory) > 4}">
		  <div class="text-center mt-3">
		    <button id="toggleCategory" class="btn btn-outline-secondary btn-sm">더보기 ▼</button>
		  </div>
		</c:if>
    </div>
  </c:if>

  <!-- 🔸 이 판매자의 다른 상품 -->
  <c:if test="${not empty sameSeller}">
    <div class="section-box">
      <h5 class="section-title"><i class="bi bi-person"></i> 이 판매자의 다른 상품</h5>
      <div class="row row-cols-1 row-cols-sm-2 row-cols-md-4 g-3" id="sellerProducts">
        <c:forEach var="item" items="${sameSeller}" varStatus="status">
          <div class="col product-item ${status.index >= 4 ? 'd-none extra-seller' : ''}">
            <a href="${pageContext.request.contextPath}/product/detail?id=${item.id}" class="text-decoration-none text-dark">
              <div class="card h-100 border-0 shadow-sm">
                <img src="${pageContext.request.contextPath}${item.displayImg}" class="card-img-top" alt="${item.title}">
                <div class="card-body">
                  <h6 class="card-title text-truncate mb-1">${item.title}</h6>
                  <small class="text-muted d-block mb-1">${item.siggName}</small>
                  <p class="card-text text-danger fw-bold mb-0">${item.sellPrice}원</p>
                </div>
              </div>
            </a>
          </div>
        </c:forEach>
      </div>
      <c:if test="${fn:length(sameSeller) > 4}">
		  <div class="text-center mt-3">
		    <button id="toggleSeller" class="btn btn-outline-secondary btn-sm">더보기 ▼</button>
		  </div>
		</c:if>

    </div>
  </c:if>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script src="<c:url value='/user/js/product_detail.js'/>"></script>
<script src="<c:url value='/user/js/wish_list.js'/>"></script>
<script>
  window.contextPath = '${pageContext.request.contextPath}';
</script>

</body>
</html>

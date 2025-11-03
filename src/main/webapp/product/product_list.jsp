<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>상품 목록</title>

<!-- Context Path 변수 설정 -->
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<link rel="stylesheet" href="${ctx}/product/resources/css/bootstrap.min.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" />
<link rel="stylesheet" href="${ctx}/product/resources/css/product_list.css">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
      rel="stylesheet"
      integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
      crossorigin="anonymous">

<style type="text/css">
	/* Styles extracted from product_list.jsp for product list page */
body {
  background-color: #f8f9fa;
  font-family: 'Noto Sans KR', sans-serif;
  margin: 0;
  padding: 0;
}

/* 상단 검색 영역 */
.select_container {
  background: #fff;
  border-radius: 16px;
  margin: 20px auto 10px;
  width: 90%;
  max-width: 1500px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  padding: 15px 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-light {
  border-radius: 30px;
  border: 1px solid #dee2e6;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

/* 메인 영역 레이아웃 */
.main_container {
  display: flex;
  justify-content: center;
  gap: 3rem;
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
}

/* 필터 영역 (왼쪽) */
.product_filter {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  width: 400px;
  padding: 20px;
  position: sticky;
  top: 100px;
  align-self: flex-start;
}

/* 상품 리스트 (오른쪽) */
.product_items {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-content: flex-start;
  gap: 1.5rem;
  flex-grow: 0;
  margin: 0 auto;
  width: 100%;
  max-width: 1400px;
}

/* 상품 카드 */
.product_item {
  border: none;
  width: 250px;
  min-width: 230px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.product_item:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0,0,0,0.1);
}

.product_img {
  height: 200px;
  object-fit: cover;
  border-radius: 10px;
}

.form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.pagination {
  margin-top: 30px;
  justify-content: center;
}

@media (max-width: 992px) {
  .main_container {
    flex-direction: column;
    align-items: center;
  }
  .product_filter {
    position: static;
    width: 100%;
    margin-bottom: 20px;
  }
}

/* Small helpers moved from inline styles */
.popular-searches-text {
  font-size: 0.85rem;
}

.price-small {
  font-size: 0.9rem;
}
	
</style>

</head>
<body>

<c:if test="${empty products}">
  <c:redirect url="/product/list" />
</c:if>

<!-- 🔍 검색 영역 -->
<div class="select_container">
  <form action="${ctx}/product/search" method="get" class="d-flex align-items-center flex-grow-1 gap-3">
    <!-- 지역 선택 -->
    <div class="d-flex align-items-center gap-2">
      <select name="sigg_area" class="form-select">
        <option value="">지역 선택</option>
        <c:forEach var="sigg" items="${userSiggs}">
          <option value="${sigg.name}">${sigg.name}</option>
        </c:forEach>
      </select>
    </div>

    <!-- 검색창 -->
    <div class="input-group flex-grow-1">
      <span class="input-group-text bg-white border-end-0">
        <span class="material-symbols-outlined">search</span>
      </span>
      <input type="text" name="q" class="form-control border-start-0" placeholder="상품명 또는 카테고리 검색">
      <button class="btn btn-primary" type="submit">
        <span class="material-symbols-outlined">arrow_circle_right</span>
      </button>
    </div>
  </form>

  <div class="mt-2 text-secondary popular-searches-text">
    인기 검색어: 노트북, 자전거, 의자, 아이폰 ...
  </div>
</div>


<!-- 🧭 본문 영역 -->
<div class="main_container">

  <!-- 왼쪽 필터 -->
  <aside class="product_filter">
    <h5 class="fw-bold mb-3">필터</h5>
    <div class="form-check mb-3">
      <input class="form-check-input" type="checkbox" id="tradeOnly">
      <label class="form-check-label" for="tradeOnly">거래 기능만 보기</label>
    </div>
    <hr>

    <!-- 위치 필터 -->
    <div class="mb-4">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h6 class="fw-bold mb-0">위치</h6>
        <a href="${ctx}/product/product_list.jsp" class="text-decoration-none text-primary small">초기화</a>
      </div>
      <div class="d-flex flex-column">
        <c:if test="${not empty userSidos}">
          <c:forEach var="sido" items="${userSidos}">
            <p class="mb-1 text-secondary">${sido.name}</p>
          </c:forEach>
        </c:if>

        <c:if test="${not empty userSiggs}">
          <c:forEach var="sigg" items="${userSiggs}">
            <div class="form-check mb-1">
              <input class="form-check-input" type="radio" name="sigg_area" id="sigg_${sigg.id}" value="${sigg.name}">
              <label class="form-check-label" for="sigg_${sigg.id}">${sigg.name}</label>
            </div>
          </c:forEach>
        </c:if>
      </div>
    </div>

    <!-- 카테고리 필터 -->
    <div class="categories">
      <h6 class="fw-bold mb-2">카테고리</h6>
      <div class="d-flex flex-column">
        <c:if test="${not empty categories}">
          <c:forEach var="cat" items="${categories}">
            <div class="form-check mb-1">
              <input class="form-check-input" type="radio" name="category" id="cat_${cat.id}" value="${cat.name}">
              <label class="form-check-label" for="cat_${cat.id}">${cat.name}</label>
            </div>
          </c:forEach>
        </c:if>
      </div>
    </div>
  </aside>

  <!-- 오른쪽 상품 목록 -->
  <section class="product_items">
    <c:choose>
      <c:when test="${empty products}">
        <div class="text-center text-secondary py-5">등록된 상품이 없습니다.</div>
      </c:when>
      <c:otherwise>
        <c:forEach var="p" items="${products}">
          <a href="${ctx}/product/detail?id=${p.id}" type="button" class="text-decoration-none">
            <div class="product_item card p-2">
              <c:choose>
                <c:when test="${not empty p.displayImg}">
                  <img src="${p.displayImg}" class="card-img-top rounded-4 product_img" alt="상품 이미지">
                </c:when>
                <c:otherwise>
                  <img src="${ctx}/resources/images/noimage.jpg" class="card-img-top rounded-4 product_img" alt="이미지없음">
                </c:otherwise>
              </c:choose>
              <div class="card-body p-2">
                <h6 class="card-title text-truncate mb-1 fw-bold">${p.title}</h6>
                <p class="mb-1 text-primary fw-semibold price-small">${p.sellPrice}원</p>
                <p class="text-muted small mb-0">${p.siggName}</p>
              </div>
            </div>
          </a>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </section>
</div>

<!-- 페이지네이션 -->
<nav aria-label="Page navigation" class="mt-4">
  <c:if test="${not empty totalPages}">
    <ul class="pagination justify-content-center">
      <li class="page-item ${page <= 1 ? 'disabled' : ''}">
        <a class="page-link" href="?page=1${preserveParams}">처음</a>
      </li>
      <li class="page-item ${page <= 1 ? 'disabled' : ''}">
        <a class="page-link" href="?page=${page-1}${preserveParams}" aria-label="Previous">&laquo;</a>
      </li>

      <c:set var="window" value="2" />
      <c:set var="start" value="${page - window}" />
      <c:if test="${start < 1}"><c:set var="start" value="1"/></c:if>
      <c:set var="end" value="${page + window}" />
      <c:if test="${end > totalPages}"><c:set var="end" value="${totalPages}"/></c:if>

      <c:forEach begin="${start}" end="${end}" var="i">
        <li class="page-item ${i == page ? 'active' : ''}">
          <c:choose>
            <c:when test="${i == page}">
              <span class="page-link">${i}</span>
            </c:when>
            <c:otherwise>
              <a class="page-link" href="?page=${i}${preserveParams}">${i}</a>
            </c:otherwise>
          </c:choose>
        </li>
      </c:forEach>

      <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
        <a class="page-link" href="?page=${page+1}${preserveParams}" aria-label="Next">&raquo;</a>
      </li>
      <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
        <a class="page-link" href="?page=${totalPages}${preserveParams}">마지막</a>
      </li>
    </ul>
  </c:if>
</nav>

</body>
</html>

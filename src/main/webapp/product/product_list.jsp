<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
<link rel="stylesheet" href="../resources/css/bootstrap.min.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" />
<link rel="stylesheet" href="../resources/css/product_list.css">
</head>
<body>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- If this JSP is opened directly (no 'products' attribute), redirect to the servlet that provides data -->
<c:if test="${empty products}">
  <c:redirect url="${pageContext.request.contextPath}/product/list" />
</c:if>

<!-- 🔍 검색 영역 -->
<div class="select_container">
  <div class="d-flex align-items-center gap-2">
    <button type="button" class="btn btn-light d-flex align-items-center gap-1 px-3 py-2">
      <span class="material-symbols-outlined">location_on</span>
      <span class="fs-6">지역 선택</span>
    </button>
  </div>
  <div class="d-flex flex-column flex-grow-1 ms-3">
    <div class="input-group">
      <span class="input-group-text bg-white border-end-0">
        <span class="material-symbols-outlined">search</span>
      </span>
      <input type="text" class="form-control border-start-0" placeholder="상품명 또는 카테고리 검색">
      <button class="btn btn-primary">
        <span class="material-symbols-outlined">arrow_circle_right</span>
      </button>
    </div>
  <div class="mt-2 text-secondary popular-searches-text">인기 검색어: 노트북, 자전거, 의자, 아이폰 ...</div>
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
        <a href="./product_list.jsp" class="text-decoration-none text-primary small">초기화</a>
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
          <a href="${pageContext.request.contextPath}/product/detail?id=${p.id}" type="button">
            <div class="product_item card p-2">
              <c:choose>
                  <c:when test="${not empty p.displayImg}">
                    <img src="${p.displayImg}" class="card-img-top rounded-4 product_img" alt="상품 이미지">
                  </c:when>
                  <c:otherwise>
                    <img src="${pageContext.request.contextPath}/resources/images/noimage.jpg" class="card-img-top rounded-4 product_img" alt="이미지없음">
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

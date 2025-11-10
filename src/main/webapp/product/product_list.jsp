<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>상품 목록</title>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 구글 아이콘 + 부트스트랩 -->
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" />
<link
  href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
  rel="stylesheet"
  integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
  crossorigin="anonymous">

<!-- 커스텀 CSS -->
<link rel="stylesheet" href="<c:url value='/user/css/product_list.css'/>">
</head>

<body class="bg-light">

	<jsp:include page="/header/header.jsp" />
<!-- 🔧 페이징 시 검색/필터 파라미터 유지용 쿼리스트링 생성 -->
<c:set var="preserveParams" value=""/>
<c:if test="${not empty param.q}">
  <c:set var="preserveParams" value="${preserveParams}&amp;q=${fn:escapeXml(param.q)}"/>
</c:if>
<c:if test="${not empty param.category}">
  <c:set var="preserveParams" value="${preserveParams}&amp;category=${fn:escapeXml(param.category)}"/>
</c:if>
<c:if test="${not empty param.sigg_area}">
  <c:set var="preserveParams" value="${preserveParams}&amp;sigg_area=${fn:escapeXml(param.sigg_area)}"/>
</c:if>
<c:if test="${not empty param.sidoId}">
  <c:set var="preserveParams" value="${preserveParams}&amp;sidoId=${fn:escapeXml(param.sidoId)}"/>
</c:if>
<c:if test="${not empty param.minPrice}">
  <c:set var="preserveParams" value="${preserveParams}&amp;minPrice=${fn:escapeXml(param.minPrice)}"/>
</c:if>
<c:if test="${not empty param.maxPrice}">
  <c:set var="preserveParams" value="${preserveParams}&amp;maxPrice=${fn:escapeXml(param.maxPrice)}"/>
</c:if>

<!-- 🔍 검색 영역 -->
<div class="select_container container py-4 d-flex flex-column align-items-center">

  <!-- 검색 폼 -->
  <form action="${ctx}/product" method="get"
        class="w-100 d-flex justify-content-center">
    <div class="input-group" style="max-width: 600px; width: 100%;">
      <span class="input-group-text bg-white border-end-0">
        <span class="material-symbols-outlined">search</span>
      </span>
      <input type="text"
             name="q"
             class="form-control border-start-0 text-center"
             placeholder="상품명 또는 카테고리 검색"
             value="<c:out value='${param.q}'/>">
      <button class="btn btn-primary" type="submit">
        <span class="material-symbols-outlined">arrow_circle_right</span>
      </button>
    </div>
  </form>

  <!-- 현재 검색어 표시 -->
  <c:if test="${not empty param.q}">
    <div class="mt-2 text-secondary small text-center"
         style="max-width: 600px; width: 100%;">
      '<strong><c:out value="${param.q}"/></strong>' 검색 결과
      <c:if test="${not empty totalCount}">
        · 총 <strong>${totalCount}</strong>개
      </c:if>
    </div>
  </c:if>
<c:if test="${empty param.q}">
  <c:choose>
    <c:when test="${not empty popularKeywords}">
      <div class="mt-2 text-secondary small text-center"
           style="max-width: 600px; width: 100%;">
        인기 검색어:
        <c:forEach var="kw" items="${popularKeywords}" varStatus="st">
          <a href="${ctx}/product?q=${fn:escapeXml(kw)}"
             class="text-decoration-none text-secondary">
            <c:out value="${kw}"/>
          </a><c:if test="${!st.last}">, </c:if>
        </c:forEach>
      </div>
    </c:when>

    <c:otherwise>
      <div class="mt-2 text-secondary small text-center"
           style="max-width: 600px; width: 100%;">
        원하는 상품명을 입력해서 검색해 보세요.
      </div>
    </c:otherwise>
  </c:choose>
</c:if>


</div>


<!-- 🧭 본문 영역 -->
<div class="main_container container d-flex gap-4">

  <!-- 왼쪽 필터 -->
  <aside class="product_filter p-3 rounded shadow-sm">
    <!-- ✅ 라우트 통합: /product -->
    <form method="get" action="${ctx}/product">
      <h5 class="fw-bold mb-3">필터</h5>
      <!-- ✅ 검색어 유지용 hidden -->
      <input type="hidden" name="q" value="${fn:escapeXml(param.q)}" />

      <c:if test="${not empty param.category or not empty param.sigg_area or not empty param.maxPrice}">
        <div class="filter-tags-box bg-white border rounded-4 shadow-sm px-3 py-2 mb-3">
          <div class="d-flex align-items-center flex-wrap gap-2">
            <span class="fw-semibold text-secondary me-2">적용된 필터</span>

            <!-- ✅ 카테고리 -->
<c:if test="${not empty selectedCategoryName}">
  <span class="active-filter badge bg-primary text-white p-2 d-flex align-items-center">
    <c:out value="${selectedCategoryName}"/>
    <button type="button" class="btn btn-sm btn-close btn-close-white remove-filter ms-2"
            data-type="category" data-value="<c:out value='${param.category}'/>"
            aria-label="카테고리 제거"></button>
  </span>
</c:if>

<!-- ✅ 시군구 -->
<c:if test="${not empty selectedSiggName}">
  <span class="active-filter badge bg-success text-white p-2 d-flex align-items-center">
    <c:out value="${selectedSiggName}"/>
    <button type="button" class="btn btn-sm btn-close btn-close-white remove-filter ms-2"
            data-type="sigg" data-value="<c:out value='${param.sigg_area}'/>"
            aria-label="시군구 제거"></button>
  </span>
</c:if>

            <!-- ✅ 가격 -->
            <c:if test="${not empty param.maxPrice}">
              <span class="active-filter badge bg-warning text-dark p-2 d-flex align-items-center">
                ~<c:out value="${param.maxPrice}"/>원
                <button type="button" class="btn btn-sm btn-close remove-filter ms-2"
                        data-type="price" data-value="<c:out value='${param.maxPrice}'/>"
                        aria-label="가격 제거"></button>
              </span>
            </c:if>

            <a href="${ctx}/product" class="ms-auto text-secondary small text-decoration-none">필터 초기화 ✖</a>
          </div>
        </div>
      </c:if>

      <!-- 위치 필터 -->
      <div class="mb-4">
        <h6 class="fw-bold mb-2">위치</h6>
        <select id="sido" name="sidoId" class="form-select mb-3">
          <option value="">시/도 선택</option>
          <c:forEach var="sido" items="${userSidos}">
            <option value="${sido.id}" ${param.sidoId == sido.id ? 'selected' : ''}>${sido.name}</option>
          </c:forEach>
        </select>

        <div id="siggContainer" class="sigg-radio-list">
          <p class="text-secondary small">시/군/구를 선택해주세요.</p>
        </div>
      </div>

      <!-- 카테고리 (※ 서버가 ID 받으면 value='${cat.id}'로) -->
      <div class="categories mb-4">
        <h6 class="fw-bold mb-2">카테고리</h6>
        <div class="d-flex flex-column">
          <c:forEach var="cat" items="${categories}">
            <div class="form-check mb-1">
              <input class="form-check-input" type="radio" name="category" id="cat_${cat.id}"
                     value="${cat.id}" ${param.category == cat.id ? 'checked' : ''}>
              <label class="form-check-label" for="cat_${cat.id}">${cat.name}</label>
            </div>
          </c:forEach>
        </div>
      </div>

      <!-- 가격 -->
      <div class="price_filter mt-4">
        <h6 class="fw-bold mb-3">가격</h6>

        <div class="d-flex flex-wrap gap-2 mb-3">
          <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="0">나눔</button>
          <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="5000">5천 이하</button>
          <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="10000">1만 이하</button>
          <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="20000">2만 이하</button>
        </div>

        <div class="d-flex align-items-center gap-2 mb-2">
          <input type="number" id="minPrice" name="minPrice" class="form-control form-control-sm text-end"
                 placeholder="0" min="0" step="1000" style="max-width: 100px;" value="<c:out value='${param.minPrice}'/>">
          <span class="text-secondary">-</span>
          <input type="number" id="maxPrice" name="maxPrice" class="form-control form-control-sm text-end"
                 placeholder="최대" min="0" step="1000" style="max-width: 100px;" value="<c:out value='${param.maxPrice}'/>">
        </div>

        <button type="submit" id="applyPrice" class="btn btn-link text-decoration-none p-0 small text-primary">
          적용하기
        </button>
      </div>
    </form>
  </aside>

  <!-- 오른쪽 상품 목록 -->
  <section class="product_items">
    <c:choose>
      <c:when test="${empty products}">
        <div class="text-center text-secondary py-5">등록된 상품이 없습니다.</div>
      </c:when>
      <c:otherwise>
        <div class="row g-3">
          <c:forEach var="p" items="${products}">
            <div class="col-6 col-md-4 col-lg-4 product_item ${p.status eq 'SOLD_OUT' ? 'soldout' : ''}">
              <a href="${ctx}/product/detail?id=${p.id}" class="text-decoration-none ${p.status eq 'SOLD_OUT' ? 'disabled-link' : ''}">
                <div class="card border-0 shadow-sm position-relative">

                  <!-- 이미지 + 배지 -->
                  <div class="image-wrapper">
                    <!-- ✅ 컨텍스트 경로 + DAO의 절대경로 -->
                    <img src="${ctx}${p.displayImg}"
                         class="card-img-top product_img ${p.status eq 'SOLD_OUT' ? 'soldout' : ''}"
                         alt="상품 이미지"
                         onerror="this.src='${ctx}/product/resources/images/noimage.jpg'">

                    <c:choose>
                      <c:when test="${p.status eq 'SOLD_OUT'}">
                        <img src="${ctx}/user/img/sold_out.png" alt="판매완료" class="soldout-image">
                      </c:when>
                      <c:when test="${p.status eq 'RESERVED'}">
                        <img src="${ctx}/user/img/reserved.png" alt="예약중" class="reserved-image">
                      </c:when>
                    </c:choose>
                  </div>

                  <!-- 상품 정보 -->
                  <div class="card-body p-2">
                    <h6 class="card-title text-truncate mb-1 fw-bold"><c:out value="${p.title}"/></h6>
                    <p class="mb-1 text-primary fw-semibold price-small">
                      <fmt:formatNumber value="${p.sellPrice}" type="number"/>원
                    </p>
                    <p class="text-muted small mb-0"><c:out value="${p.siggName}"/></p>
                  </div>

                </div>
              </a>
            </div>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</div>

<!-- 페이지네이션 -->
<nav aria-label="Page navigation" class="mt-5">
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

<script>
  const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/user/js/image-preview.js"></script>
<script src="${pageContext.request.contextPath}/user/js/product_filter.js"></script>
<script>
  window.contextPath = "${pageContext.request.contextPath}";
  window.serverParams = {
    sidoId: "${fn:escapeXml(param.sidoId)}",
    siggArea: "${fn:escapeXml(param.sigg_area)}"
  };
</script>

<jsp:include page="/footer/footer.jsp" />
</body>
</html>

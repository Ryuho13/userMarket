<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>단감 나라</title>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script src="https://cdn.tailwindcss.com"></script>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" />
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="<c:url value='/product/css/product_list.css'/>">
</head>

<body class="bg-light" data-context-path="${pageContext.request.contextPath}">

<jsp:include page="/header/header.jsp" />

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
<c:if test="${param.onlyAvailable == '1'}">
  <c:set var="preserveParams" value="${preserveParams}&amp;onlyAvailable=1"/>
</c:if>

<div class="select_container container py-4 d-flex flex-column align-items-center">
  <c:if test="${not empty param.q}">
    <div class="mt-2 text-secondary fs-4 text-center" style="max-width: 600px; width: 100%;">
      '<strong><c:out value="${param.q}"/></strong>' 검색 결과
      <c:if test="${not empty totalCount}"> · 총 <strong>${totalCount}</strong>개</c:if>
    </div>
  </c:if>

  <c:if test="${empty param.q}">
    <c:choose>
      <c:when test="${not empty popularKeywords}">
        <div class="mt-2 text-secondary fs-4 text-center" style="max-width: 600px; width: 100%;">
          인기 검색어:
          <c:forEach var="kw" items="${popularKeywords}" varStatus="st">
            <a href="${ctx}/product?q=${fn:escapeXml(kw)}" class="text-decoration-none text-secondary"><c:out value="${kw}"/></a><c:if test="${!st.last}">, </c:if>
          </c:forEach>
        </div>
      </c:when>
      <c:otherwise>
        <div class="mt-2 text-secondary fs-4 text-center" style="max-width: 600px; width: 100%;">
          원하는 상품명을 입력해서 검색해 보세요!
        </div>
      </c:otherwise>
    </c:choose>
  </c:if>
</div>

<div class="main_container container d-flex gap-4">

<div class="filter-toggle-btn-container mb-2">
  <button type="button" id="filterToggleBtn" class="filter-toggle-btn">필터 보기 ▼</button>
</div>


<aside class="product_filter p-3 rounded shadow-sm">
  <form method="get" action="${ctx}/product">
    <h5 class="fw-bold mb-3">필터</h5>
    <input type="hidden" name="q" value="${fn:escapeXml(param.q)}" />

    <c:if test="${not empty param.category or not empty param.sidoId or not empty param.sigg_area or not empty param.minPrice or not empty param.maxPrice or param.onlyAvailable=='1'}">
      <div class="filter-tags-box bg-white border rounded-4 shadow-sm px-3 py-2 mb-3">
        <div class="d-flex align-items-center flex-wrap gap-2">
          <span class="fw-semibold text-secondary me-2">적용된 필터</span>

          <c:if test="${not empty selectedCategoryName}">
            <c:url var="removeCategoryUrl" value="/product">
              <c:if test="${not empty param.q}"><c:param name="q" value="${param.q}"/></c:if>
              <c:if test="${not empty param.sidoId}"><c:param name="sidoId" value="${param.sidoId}"/></c:if>
              <c:if test="${not empty param.sigg_area}"><c:param name="sigg_area" value="${param.sigg_area}"/></c:if>
              <c:if test="${not empty param.minPrice}"><c:param name="minPrice" value="${param.minPrice}"/></c:if>
              <c:if test="${not empty param.maxPrice}"><c:param name="maxPrice" value="${param.maxPrice}"/></c:if>
              <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
              <c:param name="sort" value="${param.sort != null ? param.sort : 'latest'}"/>
              <c:param name="page" value="1"/>
            </c:url>

            <span class="active-filter badge bg-primary text-white p-2 d-flex align-items-center">
              <c:out value="${selectedCategoryName}"/>
              <a href="${removeCategoryUrl}" class="btn btn-sm btn-close btn-close-white ms-2"></a>
            </span>
          </c:if>

          <c:if test="${not empty selectedSiggName}">
            <c:url var="removeRegionUrl" value="/product">
              <c:if test="${not empty param.q}"><c:param name="q" value="${param.q}"/></c:if>
              <c:if test="${not empty param.category}"><c:param name="category" value="${param.category}"/></c:if>
              <c:if test="${not empty param.minPrice}"><c:param name="minPrice" value="${param.minPrice}"/></c:if>
              <c:if test="${not empty param.maxPrice}"><c:param name="maxPrice" value="${param.maxPrice}"/></c:if>
              <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
              <c:param name="sort" value="${param.sort != null ? param.sort : 'latest'}"/>
              <c:param name="page" value="1"/>
            </c:url>

            <span class="active-filter badge bg-success text-white p-2 d-flex align-items-center">
              <c:out value="${selectedSiggName}"/>
              <a href="${removeRegionUrl}" class="btn btn-sm btn-close btn-close-white ms-2"></a>
            </span>
          </c:if>

          <c:if test="${not empty param.minPrice or not empty param.maxPrice}">
            <c:url var="removePriceUrl" value="/product">
              <c:if test="${not empty param.q}"><c:param name="q" value="${param.q}"/></c:if>
              <c:if test="${not empty param.category}"><c:param name="category" value="${param.category}"/></c:if>
              <c:if test="${not empty param.sidoId}"><c:param name="sidoId" value="${param.sidoId}"/></c:if>
              <c:if test="${not empty param.sigg_area}"><c:param name="sigg_area" value="${param.sigg_area}"/></c:if>
              <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
              <c:param name="sort" value="${param.sort != null ? param.sort : 'latest'}"/>
              <c:param name="page" value="1"/>
            </c:url>

            <span class="active-filter badge bg-warning text-dark p-2 d-flex align-items-center">
              <c:if test="${not empty param.minPrice}"><c:out value="${param.minPrice}"/>원 ~ </c:if>
              <c:if test="${not empty param.maxPrice}">~<c:out value="${param.maxPrice}"/>원</c:if>
              <a href="${removePriceUrl}" class="btn btn-sm btn-close ms-2"></a>
            </span>
          </c:if>

          <c:if test="${param.onlyAvailable == '1'}">
            <c:url var="removeAvailUrl" value="/product">
              <c:if test="${not empty param.q}"><c:param name="q" value="${param.q}"/></c:if>
              <c:if test="${not empty param.category}"><c:param name="category" value="${param.category}"/></c:if>
              <c:if test="${not empty param.sidoId}"><c:param name="sidoId" value="${param.sidoId}"/></c:if>
              <c:if test="${not empty param.sigg_area}"><c:param name="sigg_area" value="${param.sigg_area}"/></c:if>
              <c:if test="${not empty param.minPrice}"><c:param name="minPrice" value="${param.minPrice}"/></c:if>
              <c:if test="${not empty param.maxPrice}"><c:param name="maxPrice" value="${param.maxPrice}"/></c:if>
              <c:param name="sort" value="${param.sort != null ? param.sort : 'latest'}"/>
              <c:param name="page" value="1"/>
            </c:url>

            <span class="active-filter badge bg-info text-dark p-2 d-flex align-items-center">
              거래 가능만
              <a href="${removeAvailUrl}" class="btn btn-sm btn-close ms-2"></a>
            </span>
          </c:if>

          <a href="${ctx}/product" class="ms-auto text-secondary small text-decoration-none">필터 초기화 ✖</a>
        </div>
      </div>
    </c:if>

    <div class="form-check mb-3">
      <input class="form-check-input" type="checkbox" id="onlyAvailable" name="onlyAvailable" value="1" ${param.onlyAvailable == '1' ? 'checked' : ''}>
      <label class="form-check-label" for="onlyAvailable">거래 가능 물품만 보기</label>
    </div>

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

    <div class="categories mb-4">
      <h6 class="fw-bold mb-2">카테고리</h6>
      <div class="d-flex flex-column">
        <c:forEach var="cat" items="${categories}">
          <div class="form-check mb-1">
            <input class="form-check-input" type="radio" name="category" id="cat_${cat.id}" value="${cat.id}" ${param.category == cat.id ? 'checked' : ''}>
            <label class="form-check-label" for="cat_${cat.id}">${cat.name}</label>
          </div>
        </c:forEach>
      </div>
    </div>

    <div class="price_filter mt-4">
      <h6 class="fw-bold mb-3">가격</h6>
      <div class="d-flex flex-wrap gap-2 mb-3">
        <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="10000">1만 이하</button>
        <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="50000">5만 이하</button>
        <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="100000">10만 이하</button>
        <button type="button" class="price-btn btn btn-outline-secondary btn-sm" data-value="200000">20만 이하</button>
      </div>
      <div class="d-flex align-items-center gap-2 mb-2">
        <input type="number" id="minPrice" name="minPrice" class="form-control form-control-sm text-end" placeholder="0" min="0" step="1000" style="max-width: 100px;" value="<c:out value='${param.minPrice}'/>">
        <span class="text-secondary">-</span>
        <input type="number" id="maxPrice" name="maxPrice" class="form-control form-control-sm text-end" placeholder="최대" min="0" step="1000" style="max-width: 100px;" value="<c:out value='${param.maxPrice}'/>">
      </div>
      <button type="submit" id="applyPrice" class="btn btn-link text-decoration-none p-0 small text-primary">적용하기</button>
    </div>

  </form>
</aside>

<section class="product_items">

<c:url var="latestUrl" value="/product/list">
  <c:param name="sort" value="latest" />
  <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
  <c:if test="${not empty category}"><c:param name="category" value="${category}" /></c:if>
  <c:if test="${not empty sigg_area}"><c:param name="sigg_area" value="${sigg_area}" /></c:if>
  <c:if test="${not empty minPrice}"><c:param name="minPrice" value="${minPrice}" /></c:if>
  <c:if test="${not empty maxPrice}"><c:param name="maxPrice" value="${maxPrice}" /></c:if>
  <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
</c:url>

<c:url var="viewUrl" value="/product/list">
  <c:param name="sort" value="view" />
  <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
  <c:if test="${not empty category}"><c:param name="category" value="${category}" /></c:if>
  <c:if test="${not empty sigg_area}"><c:param name="sigg_area" value="${sigg_area}" /></c:if>
  <c:if test="${not empty minPrice}"><c:param name="minPrice" value="${minPrice}" /></c:if>
  <c:if test="${not empty maxPrice}"><c:param name="maxPrice" value="${maxPrice}" /></c:if>
  <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
</c:url>

<c:url var="nameUrl" value="/product/list">
  <c:param name="sort" value="name" />
  <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
  <c:if test="${not empty category}"><c:param name="category" value="${category}" /></c:if>
  <c:if test="${not empty sigg_area}"><c:param name="sigg_area" value="${sigg_area}" /></c:if>
  <c:if test="${not empty minPrice}"><c:param name="minPrice" value="${minPrice}" /></c:if>
  <c:if test="${not empty maxPrice}"><c:param name="maxPrice" value="${maxPrice}" /></c:if>
  <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
</c:url>

<c:url var="priceLowUrl" value="/product/list">
  <c:param name="sort" value="priceLow" />
  <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
  <c:if test="${not empty category}"><c:param name="category" value="${category}" /></c:if>
  <c:if test="${not empty sigg_area}"><c:param name="sigg_area" value="${sigg_area}" /></c:if>
  <c:if test="${not empty minPrice}"><c:param name="minPrice" value="${minPrice}" /></c:if>
  <c:if test="${not empty maxPrice}"><c:param name="maxPrice" value="${maxPrice}" /></c:if>
  <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
</c:url>

<c:url var="priceHighUrl" value="/product/list">
  <c:param name="sort" value="priceHigh" />
  <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
  <c:if test="${not empty category}"><c:param name="category" value="${category}" /></c:if>
  <c:if test="${not empty sigg_area}"><c:param name="sigg_area" value="${sigg_area}" /></c:if>
  <c:if test="${not empty minPrice}"><c:param name="minPrice" value="${minPrice}" /></c:if>
  <c:if test="${not empty maxPrice}"><c:param name="maxPrice" value="${maxPrice}" /></c:if>
  <c:if test="${param.onlyAvailable == '1'}"><c:param name="onlyAvailable" value="1"/></c:if>
</c:url>

<div class="sort-bar" style="text-align:right; margin-bottom:10px;">
  <a href="${latestUrl}"    class="${sort eq 'latest'    ? 'font-bold' : ''}">최신순</a> |
  <a href="${viewUrl}"      class="${sort eq 'view'      ? 'font-bold' : ''}">조회수순</a> |
  <a href="${nameUrl}"      class="${sort eq 'name'      ? 'font-bold' : ''}">이름순</a> |
  <a href="${priceLowUrl}"  class="${sort eq 'priceLow'  ? 'font-bold' : ''}">가격낮은순</a> |
  <a href="${priceHighUrl}" class="${sort eq 'priceHigh' ? 'font-bold' : ''}">가격높은순</a>
</div>

<c:choose>
  <c:when test="${empty products}">
    <div class="text-center text-secondary py-5">등록된 상품이 없습니다.</div>
  </c:when>
  <c:otherwise>
    <div class="row g-3">
      <c:forEach var="p" items="${products}">
        <div class="col-6 col-md-4 col-lg-3 product_item ${p.status eq 'SOLD_OUT' ? 'soldout' : ''}">
          <a href="${ctx}/product/detail?id=${p.id}" class="text-decoration-none ${p.status eq 'SOLD_OUT' ? 'disabled-link' : ''}">
            <div class="card border-0 shadow-sm position-relative">
              <div class="image-wrapper">
                <img src="${ctx}${p.displayImg}" class="card-img-top product_img ${p.status eq 'SOLD_OUT' ? 'soldout' : ''}" alt="상품 이미지" onerror="this.src='${ctx}/product/resources/images/noimage.jpg'">
                <c:choose>
                  <c:when test="${p.status eq 'SOLD_OUT'}">
                    <img src="${ctx}/user/img/sold_out.png" alt="판매완료" class="soldout-image">
                  </c:when>
                  <c:when test="${p.status eq 'RESERVED'}">
                    <img src="${ctx}/user/img/reserved.png" alt="예약중" class="reserved-image">
                  </c:when>
                </c:choose>
              </div>
              <div class="card-body p-2">
                <h6 class="card-title text-truncate mb-1 fw-bold"><c:out value="${p.title}"/></h6>
                <p class="mb-1 text-primary fw-semibold price-small"><fmt:formatNumber value="${p.sellPrice}" type="number"/>원</p>
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

<nav aria-label="Page navigation" class="mt-5">
<c:if test="${not empty totalPages}">
  <ul class="pagination justify-content-center">
    <li class="page-item ${page <= 1 ? 'disabled' : ''}">
      <a class="page-link" href="?page=1${preserveParams}">처음</a>
    </li>
    <li class="page-item ${page <= 1 ? 'disabled' : ''}">
      <a class="page-link" href="?page=${page-1}${preserveParams}">&laquo;</a>
    </li>

    <c:set var="window" value="2" />
    <c:set var="start" value="${page - window}" />
    <c:if test="${start < 1}"><c:set var="start" value="1"/></c:if>
    <c:set var="end" value="${page + window}" />
    <c:if test="${end > totalPages}"><c:set var="end" value="${totalPages}"/></c:if>

    <c:forEach begin="${start}" end="${end}" var="i">
      <li class="page-item ${i == page ? 'active' : ''}">
        <c:choose>
          <c:when test="${i == page}"><span class="page-link">${i}</span></c:when>
          <c:otherwise><a class="page-link" href="?page=${i}${preserveParams}">${i}</a></c:otherwise>
        </c:choose>
      </li>
    </c:forEach>

    <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
      <a class="page-link" href="?page=${page+1}${preserveParams}">&raquo;</a>
    </li>
    <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
      <a class="page-link" href="?page=${totalPages}${preserveParams}">마지막</a>
    </li>
  </ul>
</c:if>
</nav>

<script>const contextPath = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/product/js/image-preview.js"></script>
<script src="${pageContext.request.contextPath}/product/js/product_filter.js"></script>

<script>
window.contextPath = "${pageContext.request.contextPath}";
window.serverParams = {
  sidoId: "${fn:escapeXml(param.sidoId)}",
  siggArea: "${fn:escapeXml(param.sigg_area)}"
};
</script>

<jsp:include page="/footer/footer.jsp" />
<jsp:include page="/resources/alarm.jsp" />

</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<link rel="stylesheet" href="<c:url value='/user/css/product_list.css'/>">

</head>
<body class="bg-light">

<!-- 🔍 검색 영역 -->
<div class="select_container container py-4">
  <form action="${ctx}/product/search" method="get" class="d-flex align-items-center gap-3">
    <div class="d-flex align-items-center gap-2">
      <select name="sigg_area" class="form-select">
        <option value="">지역 선택</option>
        <c:forEach var="sigg" items="${userSiggs}">
          <option value="${sigg.name}">${sigg.name}</option>
        </c:forEach>
      </select>
    </div>

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

  <div class="mt-2 text-secondary small">
    인기 검색어: 노트북, 자전거, 의자, 아이폰 ...
  </div>
</div>

<!-- 🧭 본문 영역 -->
<div class="main_container container d-flex gap-4">

  <!-- 왼쪽 필터 -->
  <aside class="product_filter bg-white p-3 rounded shadow-sm">
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
    <a href="${ctx}/product/list" class="text-decoration-none text-primary small">초기화</a>
  </div>

  <!-- 🔹 시/도 선택 -->
  <select id="sidoSelect" class="form-select mb-3">
    <option value="">시/도 선택</option>
    <c:forEach var="sido" items="${userSidos}">
      <option value="${sido.id}">${sido.name}</option>
    </c:forEach>
  </select>

  <!-- 🔹 시군구 표시 영역 -->
  <div id="siggContainer" class="border rounded p-2 bg-light" style="max-height: 220px; overflow-y: auto;">
    <p class="text-muted small mb-0 text-center">시/도를 먼저 선택하세요.</p>
  </div>
</div>

<!-- ✅ 안전한 JS 구성 -->
<script>
  const siggData = {};

  // 🔹 JSP로 전달된 모든 시군구를 JS 객체에 추가
  <c:forEach var="sigg" items="${userSiggs}">
    if (!siggData["${sigg.sidoAreaId}"]) {
      siggData["${sigg.sidoAreaId}"] = [];
    }
    siggData["${sigg.sidoAreaId}"].push({
      id: "${sigg.id}",
      name: "${sigg.name}"
    });
  </c:forEach>

  const sidoSelect = document.getElementById('sidoSelect');
  const siggContainer = document.getElementById('siggContainer');

  sidoSelect.addEventListener('change', () => {
    const sidoId = sidoSelect.value;
    siggContainer.innerHTML = '';

    if (!sidoId || !siggData[sidoId]) {
      siggContainer.innerHTML = '<p class="text-muted small mb-0 text-center">시/도를 먼저 선택하세요.</p>';
      return;
    }

    siggData[sidoId].forEach(sigg => {
      const div = document.createElement('div');
      div.className = 'form-check mb-1';
      div.innerHTML = `
        <input class="form-check-input" type="radio" name="sigg_area" id="sigg_${sigg.id}" value="${sigg.name}">
        <label class="form-check-label" for="sigg_${sigg.id}">${sigg.name}</label>
      `;
      siggContainer.appendChild(div);
    });
  });
</script>


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
          <div class="product_item">
            <a href="${ctx}/product/detail?id=${p.id}" class="text-decoration-none">
              <div class="card border-0 shadow-sm">
                <c:choose>
                  <c:when test="${not empty p.displayImg}">
                      <img src="${p.displayImg}"
						 class="card-img-top product_img" alt="상품 이미지">
                  </c:when>
                  <c:otherwise>
                    <img src="${ctx}/product/resources/images/noimage.jpg" class="card-img-top product_img" alt="이미지없음">
                  </c:otherwise>
                </c:choose>
                <div class="card-body p-2">
                  <h6 class="card-title text-truncate mb-1 fw-bold">${p.title}</h6>
                  <p class="mb-1 text-primary fw-semibold price-small">${p.sellPrice}원</p>
                  <p class="text-muted small mb-0">${p.siggName}</p>
                </div>
              </div>
            </a>
          </div>
        </c:forEach>
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

</body>
</html>

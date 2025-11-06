<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<c:choose>
  <c:when test="${empty products}">
    <div class="text-center text-secondary py-5">등록된 상품이 없습니다.</div>
  </c:when>
  <c:otherwise>
    <div class="row g-3">
      <c:forEach var="p" items="${products}">
        <div class="col-6 col-md-4 col-lg-3">
          <a href="${ctx}/product/detail?id=${p.id}" class="text-decoration-none">
            <div class="card border-0 shadow-sm">
              <img src="${p.displayImg}" class="card-img-top" alt="">
              <div class="card-body p-2">
                <h6 class="text-truncate">${p.title}</h6>
                <p class="fw-semibold text-primary mb-0">${p.sellPrice}원</p>
              </div>
            </div>
          </a>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

</body>
</html>
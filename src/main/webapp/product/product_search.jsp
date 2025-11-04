<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 검색 결과</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
</head>
<body>
<div class="container py-4" style="max-width:1200px;">
  <h3>검색 결과</h3>
  <p>검색어: <strong>${query}</strong> &nbsp; 동네: <strong>${sigg}</strong></p>
  <a href="${pageContext.request.contextPath}/product/list" class="btn btn-secondary mb-3">목록으로</a>

  <div class="d-flex flex-wrap">
    <c:forEach var="p" items="${products}">
      <a href="${pageContext.request.contextPath}/product/detail?id=${p.id}" class="text-decoration-none text-secondary-emphasis m-2">
        <div class="product_item border rounded-4 shadow-sm p-3" style="width:250px; height:300px;">
          <img alt="제품 이미지" src="${pageContext.request.contextPath}${p.displayImg}" class="rounded-4 w-100 h-75">
          <p class="fw-bold mt-2 mb-0">${p.title}</p>
          <p class="fw-bold mt-1 mb-0 text-primary">${p.sellPrice}원</p>
          <p class="text-secondary mt-1 mb-0">${p.siggName}</p>
        </div>
      </a>
    </c:forEach>
  </div>
</div>
</body>
</html>

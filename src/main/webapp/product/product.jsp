<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>상품 상세</title>
    <link rel="stylesheet" href="../resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="../resources/css/product_list.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" />
</head>
<body>

<c:choose>
  <c:when test="${empty product}">
    <div class="container mt-4">
      <div class="alert alert-warning">찾을 수 없는 상품입니다.</div>
      <a href="${pageContext.request.contextPath}/product/list" class="btn btn-primary">목록으로</a>
    </div>
  </c:when>
  <c:otherwise>
    <div class="container py-4">
      <a href="${pageContext.request.contextPath}/product/list" class="btn btn-secondary mb-3">목록으로</a>
      <div class="row">
        <div class="col-md-6">
          <div>
            <c:choose>
              <c:when test="${empty product.images}">
                <img src="${pageContext.request.contextPath}/resources/images/noimage.jpg" class="img-fluid rounded" alt="이미지 없음">
              </c:when>
              <c:otherwise>
                <c:forEach var="img" items="${product.images}">
                  <img src="${img}" class="img-fluid rounded mb-2" alt="상품 이미지">
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <div class="col-md-6">
          <h2 class="fw-bold">${product.title}</h2>
          <p class="text-muted">지역: ${product.sellerSigg}</p>
          <p class="fs-4 text-danger fw-bold">${product.sellPrice}원</p>
          <p class="mt-3">${product.description}</p>

          <hr>
          <h5>판매자 정보</h5>
          <p>연락처: ${product.sellerMobile}</p>
          <p>평점: ${product.sellerRating != null ? product.sellerRating : '-'}</p>

          <div class="mt-3">
            <a href="#" class="btn btn-primary">채팅으로 문의</a>
            <a href="#" class="btn btn-outline-secondary">찜</a>
          </div>
        </div>
      </div>
    </div>
  </c:otherwise>
</c:choose>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>상품 상세</title>
    <link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/user/css/product_list.css">
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
        <!-- 상품 이미지 영역 -->
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

        <!-- 상품 상세 정보 -->
        <div class="col-md-6">
          <h2 class="fw-bold">${product.title}</h2>
          <p class="text-muted">지역: ${product.sellerSigg}</p>
          <p class="fs-4 text-danger fw-bold">${product.sellPrice}원</p>
          <p class="mt-3">${product.description}</p>

          <hr>
          <h5>판매자 정보</h5>
          <p>연락처: ${product.sellerMobile}</p>
          <p>평점: ${product.sellerRating != null ? product.sellerRating : '-'}</p>

          <div class="mt-3 d-flex gap-2">
            <!-- ✅ 로그인 여부에 따라 다르게 표시 -->
            <c:choose>
              <c:when test="${not empty sessionScope.userId}">
                <!-- 로그인 되어있으면 채팅방 생성 -->
                <form action="${pageContext.request.contextPath}/chat/chat.jsp" method="post">
                  <input type="hidden" name="buyerId" value="${sessionScope.userId}">
                  <input type="hidden" name="productId" value="${product.id}">
                  <button type="submit" class="btn btn-primary">채팅으로 문의</button>
                </form>
              </c:when>

              <c:otherwise>
                <!-- 로그인 안 되어 있으면 로그인 페이지로 -->
                <a href="${pageContext.request.contextPath}/user/login.jsp" 
                   class="btn btn-outline-primary">로그인 후 채팅하기</a>
              </c:otherwise>
            </c:choose>

            <a href="#" class="btn btn-outline-secondary">찜</a>
          </div>
        </div>
      </div>
    </div>
  </c:otherwise>
</c:choose>

</body>
</html>

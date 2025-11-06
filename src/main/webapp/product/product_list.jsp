<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>상품 목록</title>

  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="<c:url value='/user/css/product_list.css'/>">
</head>
<body class="bg-light">

  <%@ include file="_search.jsp" %>

  <div class="main_container container d-flex gap-4">
    <%@ include file="_filter.jsp" %>

    <section class="product_items flex-grow-1">
      <%@ include file="_items.jsp" %>
    </section>
  </div>

  <%@ include file="_pagination.jsp" %>

  <script src="${pageContext.request.contextPath}/user/js/product_filter.js"></script>
</body>
</html>

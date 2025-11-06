<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<aside class="product_filter bg-white p-3 rounded shadow-sm">
  <form method="get" action="${ctx}/product/list">
    <h5 class="fw-bold mb-3">필터</h5>

    <!-- 위치 -->
    <select name="sidoId" class="form-select mb-3">
      <option value="">시/도 선택</option>
      <c:forEach var="sido" items="${userSidos}">
        <option value="${sido.id}" ${param.sidoId == sido.id ? 'selected' : ''}>${sido.name}</option>
      </c:forEach>
    </select>

    <!-- 카테고리 -->
    <div class="mb-3">
      <h6>카테고리</h6>
      <c:forEach var="cat" items="${categories}">
        <div class="form-check">
          <input class="form-check-input" type="radio" name="category" value="${cat.name}" ${param.category == cat.name ? 'checked' : ''}>
          <label class="form-check-label">${cat.name}</label>
        </div>
      </c:forEach>
    </div>

    <!-- 가격 -->
    <div class="mb-3">
      <h6>가격</h6>
      <input type="number" name="minPrice" placeholder="최소" value="${param.minPrice}" class="form-control mb-2">
      <input type="number" name="maxPrice" placeholder="최대" value="${param.maxPrice}" class="form-control">
      <button type="submit" class="btn btn-link text-primary small mt-2 p-0">적용</button>
    </div>
  </form>
</aside>

</body>
</html>
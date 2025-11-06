<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="${ctx}/product/search" method="get" class="container py-4 d-flex align-items-center gap-3">
  <select name="sigg_area" class="form-select" style="max-width: 150px;">
    <option value="">지역 선택</option>
    <c:forEach var="sigg" items="${userSiggs}">
      <option value="${sigg.name}">${sigg.name}</option>
    </c:forEach>
  </select>

  <div class="input-group flex-grow-1">
    <span class="input-group-text bg-white border-end-0">
      <span class="material-symbols-outlined">search</span>
    </span>
    <input type="text" name="q" class="form-control border-start-0" placeholder="상품명 또는 카테고리 검색">
    <button class="btn btn-primary" type="submit">검색</button>
  </div>
</form>

</body>
</html>
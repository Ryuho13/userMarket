<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<c:if test="${totalPages > 1}">
  <nav aria-label="Page navigation" class="mt-5">
    <ul class="pagination justify-content-center">
      <c:forEach begin="1" end="${totalPages}" var="i">
        <li class="page-item ${i == page ? 'active' : ''}">
          <a class="page-link" href="?page=${i}${preserveParams}">${i}</a>
        </li>
      </c:forEach>
    </ul>
  </nav>
</c:if>

</body>
</html>
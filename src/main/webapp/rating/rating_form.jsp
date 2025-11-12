<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>판매자 평가하기</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
  <div class="container py-5">
    <div class="card mx-auto" style="max-width: 500px;">
      <div class="card-body">
        <h3 class="card-title mb-4">판매자 평가하기</h3>

        <form action="${pageContext.request.contextPath}/rating/save" method="post">
          <input type="hidden" name="productId" value="${productId}" />

          <div class="mb-3">
            <label class="form-label">평점 (1~5점)</label>
            <select name="rating" class="form-select" required>
              <option value="">선택하세요</option>
              <option value="1">1점</option>
              <option value="2">2점</option>
              <option value="3">3점</option>
              <option value="4">4점</option>
              <option value="5">5점</option>
            </select>
          </div>

          <div class="mb-3">
            <label class="form-label">코멘트 (선택)</label>
            <textarea name="comment" class="form-control" rows="3"
                      placeholder="거래는 어떠셨나요?"></textarea>
          </div>

          <div class="d-flex justify-content-between">
            <a href="${pageContext.request.contextPath}/product/detail?id=${productId}"
               class="btn btn-outline-secondary">뒤로가기</a>
            <button type="submit" class="btn btn-success">등록</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</body>
</html>

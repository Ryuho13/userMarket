<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>상품 상세(테스트)</title></head>
<body>
  <h2>상품 상세 (테스트)</h2>
  <p>상품명: 샘플 상품</p>
  <p>가격: 10000원</p>
  <p>판매자 ID: 1</p>

  <form action="<%=request.getContextPath()%>/chatRoom" method="post">
    <input type="hidden" name="productId" value="1">
    <input type="hidden" name="buyerId" value="999">
    <button type="submit">채팅하기</button>
  </form>
</body>
</html>

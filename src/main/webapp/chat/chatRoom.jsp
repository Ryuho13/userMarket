<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/chatRoom.css">
<script src="${pageContext.request.contextPath}/resources/js/chatRoom.js"></script>
</head>
<body data-context-path="${pageContext.request.contextPath}" data-room-id="${room.id}">



<c:if test="${empty room}">
  <h3 style="color:red;">채팅방 정보를 불러올 수 없습니다.</h3>
</c:if>

<c:if test="${not empty room}">
  <div class="chat-header">
      <button onclick="history.back()" class="back-button">&lt;</button>
      <h2>${otherUserNickname}</h2>
      <div class="spacer"></div>
  </div>

  <div id="chatBox">
    <c:if test="${not empty product}">
        <div class="product-info-bar">
            <div class="product-image">
                <c:choose>
                    <c:when test="${not empty product.images}">
                        <img src="${pageContext.request.contextPath}/upload/product_images/${product.images[0]}" alt="상품 이미지">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/product/resources/images/noimage.jpg" alt="이미지 없음">
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="product-details">
                <div class="product-title">${product.title}</div>
                <div class="product-price"><fmt:formatNumber value="${product.sellPrice}" type="number"/>원</div>
            </div>
        </div>
    </c:if>
    <c:choose>
      <c:when test="${not empty messages}">
        <c:forEach var="msg" items="${messages}">
          <%-- 내가 보낸 메시지인지 여부 판단 --%>
          <c:set var="isMine" value="${msg.senderId == sessionScope.loginUserId}" />
          <div class="chat-row ${isMine ? 'my-message' : 'other-message'}">
            <div class="bubble">
              <c:choose>
                <c:when test="${msg.message.startsWith('IMG::')}">
                  <c:set var="imageUrl" value="${msg.message.substring(5)}" />
                  <img src="${pageContext.request.contextPath}${imageUrl}" class="chat-image" alt="채팅 이미지" />
                </c:when>
                <c:otherwise>
                  <span class="message-text">${msg.message}</span>
                </c:otherwise>
              </c:choose>
              <span class="time">
                <%-- 날짜 포맷팅은 fmt 라이브러리 사용 --%>
                <fmt:formatDate value="${msg.createdAt}" pattern="HH:mm" />
              </span>
            </div>
          </div>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <p style="color:gray; text-align:center;">이전 대화가 없습니다.</p>
      </c:otherwise>
    </c:choose>
  </div>
  
  <div class="input-area">
    <label for="imageUpload" class="upload-btn">+</label>
    <input type="file" id="imageUpload" accept="image/*" style="display: none;">
    <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
    <button id="sendBtn">보내기</button>
  </div>
  
  <input type="hidden" id="roomId" value="${room.id}">
  
</c:if>

<jsp:include page="../resources/alarm.jsp" />

</body>
</html>
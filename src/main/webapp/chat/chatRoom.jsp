<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/chatRoom.css">
<script src="${pageContext.request.contextPath}/resources/chatRoom.js"></script>
</head>
<body>

<c:set var="userId" value="${sessionScope.loginUserId}" />

<c:if test="${empty room}">
  <h3 style="color:red;">채팅방 정보를 불러올 수 없습니다.</h3>
</c:if>

<c:if test="${not empty room}">
  <h2>${room.id}</h2>

  <div id="chatBox">
    <c:choose>
      <c:when test="${not empty messages}">
        <c:forEach var="msg" items="${messages}">
          <%-- 내가 보낸 메시지인지 여부 판단 --%>
          <c:set var="isMine" value="${msg.senderId == userId}" />
          <div class="chat-row ${isMine ? 'my-message' : 'other-message'}">
            <div class="bubble">
              <span class="message-text">${msg.message}</span>
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
    <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
    <button id="sendBtn">보내기</button>
  </div>
  
  <input type="hidden" id="roomId" value="${room.id}">
  <input type="hidden" id="userId" value="${userId}">
</c:if>

</body>
</html>
<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/chatRoom.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body data-context-path="${pageContext.request.contextPath}" data-room-id="${room.id}" data-user-id="${sessionScope.loginUserId}">



<c:if test="${empty room}">
  <h3 style="color:red;">채팅방 정보를 불러올 수 없습니다.</h3>
</c:if>

<c:if test="${not empty room}">
  <!-- 🔴 chat-element-width 클래스 추가 -->
  <div class="chat-header chat-element-width">
      <!-- 🔴 mb-4 (마진) 클래스 제거 -->
      <button type="back" onclick="history.back()" class="btn btn-outline-muted btn-sm2">
          <i class="fas fa-arrow-left"></i> 뒤로가기
      </button>
      <h2>${otherUserNickname}</h2>
      <div class="spacer"></div>
  </div>

  <!-- 🔴 chat-element-width 클래스 추가 -->
  <div id="chatBox" class="chat-element-width">
    <c:if test="${not empty product}">
        <div class="product-info-bar">
            <div class="product-image">
                <c:choose>
                    <c:when test="${not empty product.images}">
                        <img src="${pageContext.request.contextPath}/upload/product_images/${product.images[0]}" alt="상품 이미지" class="chat-image" onclick="openImageModal(this.src)">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/product/resources/images/noimage.jpg" alt="이미지 없음" class="chat-image" onclick="openImageModal(this.src)">
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
                  <img src="${pageContext.request.contextPath}${imageUrl}" class="chat-image" alt="채팅 이미지" onclick="openImageModal(this.src)" />
                </c:when>
                <c:otherwise>
                  <span class="message-text">${msg.message}</span>
                </c:otherwise>
              </c:choose>
              <span class="time"><fmt:formatDate value="${msg.createdAt}" pattern="HH:mm" /></span>
            </div>
          </div>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <p style="color:gray; text-align:center;">이전 대화가 없습니다.</p>
      </c:otherwise>
    </c:choose>
  </div>
  
  <!-- 🔴 chat-element-width 클래스 추가 -->
  <div class="input-area chat-element-width">
    <!-- '+' 대신 아이콘 사용 (선택 사항) -->
    <label for="imageUpload" class="upload-btn"><i class="fas fa-image"></i></label>
    <input type="file" id="imageUpload" accept="image/*" style="display: none;">
    <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
    <button id="sendBtn">보내기</button>
  </div>
  
  <input type="hidden" id="roomId" value="${room.id}">
  
</c:if>

<jsp:include page="../resources/alarm.jsp" />

<!-- 이미지 모달 -->
<div id="imageModal" class="image-modal hidden">
  <span class="close-button">&times;</span>
  <img class="modal-content" id="modalImage">
</div>

<!-- 🔴 스크립트 추가 (버튼 크기 측정 및 spacer에 적용) -->
<script>
    // JSP 환경에서는 버튼 크기가 로드 후 결정되므로, JS로 spacer 너비를 설정합니다.
    window.onload = function() {
        const backButton = document.querySelector('.btn-outline-muted');
        const spacer = document.querySelector('.spacer');
        // 버튼의 실제 계산된 너비를 spacer에 적용하여 완벽한 중앙 정렬을 보장합니다.
        spacer.style.width = backButton.offsetWidth + 'px';

        // Font Awesome 아이콘으로 변경 (JSP 파일에는 Font Awesome이 link 태그로 추가되어 있으므로 적용)
        const backButtonText = document.querySelector('.btn-outline-muted');
        backButtonText.innerHTML = '<i class="fas fa-arrow-left"></i> 뒤로가기';
    };
</script>
<script src="${pageContext.request.contextPath}/resources/js/chatRoom.js"></script>
</body>
</html>
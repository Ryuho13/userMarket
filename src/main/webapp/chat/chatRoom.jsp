<%@ page import="model.ChatRoom" %>
<%@ page import="java.util.*" %>
<%@ page import="model.Message" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>

<!-- 외부 CSS 연결 -->
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/chatRoom.css">

<!-- 외부 JS 연결 -->
<script src="<%= request.getContextPath() %>/resources/chatRoom.js"></script>
</head>

<body>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  List<Message> messages = (List<Message>) request.getAttribute("messages");
  long roomId = room != null ? room.getId() : 1L;
  long userId = 999L; // 로그인 미구현 시 테스트용 고정 ID
%>

<h2 style="text-align:center;">채팅방 ID: <%= roomId %></h2>

<!-- 채팅 메시지 표시 영역 -->
<div id="chatBox">
  <% if (messages != null) { 
       for (Message msg : messages) { 
          boolean isMine = msg.getSenderId() == userId;
  %>
        <div class="<%= isMine ? "my-message" : "other-message" %>">
          [<%= msg.getSenderId() %>] <%= msg.getMessage() %>
        </div>
  <%   }
     } else { %>
     <p style="color:#aaa; text-align:center;">이전 대화가 없습니다.</p>
  <% } %>
</div>

<!-- 입력창 영역 -->
<div id="inputArea">
  <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
  <button id="sendBtn">보내기</button>
</div>

<!-- 숨겨진 데이터 -->
<input type="hidden" id="roomId" value="<%= roomId %>">
<input type="hidden" id="userId" value="<%= userId %>">

</body>
</html>

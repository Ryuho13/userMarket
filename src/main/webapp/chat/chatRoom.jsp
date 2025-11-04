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

<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/chatRoom.css">
<script src="<%= request.getContextPath() %>/resources/chatRoom.js"></script>
</head>

<body>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  List<Message> messages = (List<Message>) request.getAttribute("messages");
  long roomId = room != null ? room.getId() : 1L;
  long userId = 99L; // 로그인 미구현 시 테스트용
%>

<h2 style="text-align:center;">채팅방 ID: <%= roomId %></h2>

<!-- 채팅박스 -->
<div id="chatBox">
  <% if (messages != null && !messages.isEmpty()) { %>
    <% for (Message msg : messages) {
         boolean isMine = msg.getSenderId() == userId;
    %>
    <div class="<%= isMine ? "my-message" : "other-message" %>">
    <span class="message-text"><%= msg.getMessage() %></span>
    <span class="message-time">
    <%
  		if (msg.getCreatedAt() != null) {
      	String timeStr = msg.getCreatedAt().toString();
      	out.print(timeStr.substring(11, 16)); // HH:mm
  		}
		%>
        </span>
      </div>
    <% } %>
  		<% } else { %>
  				<p style="color:#999; text-align:center;">💬 이전 대화가 없습니다.</p>
  				<% } %>
</div>



<!-- 입력 영역 -->
<div id="inputArea">
  <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
  <button id="sendBtn">보내기</button>
</div>

<!-- 숨겨진 데이터 -->
<input type="hidden" id="roomId" value="<%= roomId %>">
<input type="hidden" id="userId" value="<%= userId %>">

</body>
</html>

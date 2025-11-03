<%@ page import="java.util.*" %>
<%@ page import="model.Message" %>
<%@ page import="model.ChatRoom" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>

<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/chatRoom.css">
<script src="<%= request.getContextPath() %>/resources/js/chatRoom.js" defer></script>
</head>

<body>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  List<Message> messages = (List<Message>) request.getAttribute("messages");
  long roomId = room != null ? room.getId() : 1L;
  long userId = 999L; // 로그인 없이 테스트용
%>

<h2>채팅방: <%= roomId %></h2>

<div id="chatBox" data-room="<%= roomId %>" data-user="<%= userId %>">
  <% if (messages != null && !messages.isEmpty()) {
       for (Message msg : messages) { %>
    <div class="<%= (msg.getSenderId() == userId ? "my-message" : "other-message") %>">
      <%= msg.getMessage() %>
    </div>
  <% } } %>
</div>

<div class="chat-input-area">
  <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
  <button id="sendBtn">보내기</button>
</div>

</body>
</html>

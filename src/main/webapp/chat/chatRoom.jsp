<%@ page import="model.ChatRoom" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  long roomId = room != null ? room.getId() : 1L;
  long userId = 2L; // 로그인 미구현 테스트용
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/chatRoom.css">
<script src="<%= request.getContextPath() %>/resources/chatRoom.js" defer></script>
</head>
<body>

  <h2>채팅방: <%= roomId %></h2>
  <div id="chatBox" data-room="<%= roomId %>" data-user="<%= userId %>"></div>
  <div class="chat-input-area">
  	<textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
  	<button id="sendBtn" onclick="sendMessage('<%= roomId %>', '<%= userId %>')">보내기</button>
	</div>

</body>
</html>
    
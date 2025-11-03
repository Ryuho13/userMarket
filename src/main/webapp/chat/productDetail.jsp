<%@ page import="model.ChatRoom" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/chatRoom.css">
</head>

<body>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  long roomId = room != null ? room.getId() : 1L; // room이 없을 때 대비
  long userId = 999L; // 로그인 없이 테스트용 ID
%>

<h2>USER ID: <%= roomId %></h2>

<div id="chatBox"></div>

<div class="chat-input-area">
  <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
  <button id="sendBtn" onclick="sendMessage('<%= roomId %>', '<%= userId %>')">보내기</button>
</div>

<!-- 숨겨진 데이터 -->
<input type="hidden" id="roomId" value="<%= roomId %>">
<input type="hidden" id="userId" value="<%= userId %>">

</body>
</html>

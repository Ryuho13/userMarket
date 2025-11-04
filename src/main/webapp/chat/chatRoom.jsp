<%@ page import="java.util.List" %>
<%@ page import="model.Message" %>
<%@ page import="model.ChatRoom" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/chatRoom.css">
<script src="<%=request.getContextPath()%>/resources/chatRoom.js"></script>
</head>
<body>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  List<Message> messages = (List<Message>) request.getAttribute("messages");
  long userId = 999L; // 로그인 미구현 시 임시 ID

  if (room == null) {
%>
  <h3 style="color:red;">채팅방 정보를 불러올 수 없습니다.</h3>
<%
    return;
  }
%>

<h2><%= room.getId() %></h2>

<div id="chatBox">
  <% if (messages != null && !messages.isEmpty()) { %>
    <% for (Message msg : messages) { 
         boolean isMine = msg.getSenderId() == userId; %>
      <div class="chat-row <%= isMine ? "my-message" : "other-message" %>">
        <div class="bubble">
          <span class="message-text"><%= msg.getMessage() %></span>
          <span class="time">
            <%= msg.getCreatedAt() != null 
                  ? new java.text.SimpleDateFormat("HH:mm").format(msg.getCreatedAt()) 
                  : "" %>
          </span>
        </div>
      </div>
    <% } %>
  <% } else { %>
    <p style="color:gray; text-align:center;">이전 대화가 없습니다.</p>
  <% } %>
</div>


<div class="input-area">
  <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
  <button id="sendBtn">보내기</button>
</div>

<input type="hidden" id="roomId" value="<%= room.getId() %>">
<input type="hidden" id="userId" value="<%= userId %>">

</body>
</html>

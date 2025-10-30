<%@ page import="model.ChatRoom" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>채팅창</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/chatRoom.css">

<script>
let ws;

function connect(roomId, userId) {
  const wsUrl = "ws://" + window.location.host + "<%= request.getContextPath() %>/chatSocket/" + roomId + "/" + userId;
  ws = new WebSocket(wsUrl);

  ws.onopen = () => console.log("WebSocket 연결 성공");

  ws.onmessage = (event) => {
    const chatBox = document.getElementById("chatBox");
    const text = event.data;

    // 메시지 앞부분: [999] : 내용
    const isMine = text.startsWith("[" + userId + "]");

    const msgDiv = document.createElement("div");
    msgDiv.className = isMine ? "my-message" : "other-message";

    const cleanText = text.replace("[" + userId + "] : ", ""); // 내 ID 제거
    msgDiv.textContent = cleanText;

    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
  };

  ws.onclose = () => console.log("WebSocket 연결 종료");
  ws.onerror = (err) => console.error("WebSocket 오류", err);
}

function sendMessage(roomId, userId) {
  const input = document.getElementById("msg");
  const msg = input.value.trim();
  if (msg === "") return;
  ws.send(msg);
  input.value = "";
}

window.onload = function() {
  const roomId = document.getElementById("roomId").value;
  const userId = document.getElementById("userId").value;
  connect(roomId, userId);

  // 엔터로 전송 / Shift+Enter 줄바꿈
  const msgInput = document.getElementById("msg");
  msgInput.addEventListener("keydown", function(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage(roomId, userId);
    }
  });
};
</script>
</head>

<body>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  long roomId = room != null ? room.getId() : 1L;
  long userId = 2L; // 로그인 미구현 테스트용 ID
%>

<h2>USER ID: <%= roomId %></h2>

<div id="chatBox"></div>

<div>
  <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)"></textarea>
  <button onclick="sendMessage('<%= roomId %>', '<%= userId %>')">보내기</button>
</div>

<input type="hidden" id="roomId" value="<%= roomId %>">
<input type="hidden" id="userId" value="<%= userId %>">
</body>
</html>

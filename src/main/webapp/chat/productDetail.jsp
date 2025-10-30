<%@ page import="model.ChatRoom" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>실시간 채팅방</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/chatRoom.css">

<script>
let ws;

function connect(roomId, userId) {
  const wsUrl = "ws://" + window.location.host + "<%= request.getContextPath() %>/chatSocket/" + roomId + "/" + userId;
  ws = new WebSocket(wsUrl);

  ws.onopen = () => console.log("WebSocket 연결 성공");
  ws.onmessage = (event) => {
    const chatBox = document.getElementById("chatBox");
    const msgDiv = document.createElement("div");
    msgDiv.textContent = event.data;
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

  //  엔터로 전송 / Shift+Enter 줄바꿈
  const msgInput = document.getElementById("msg");
  msgInput.addEventListener("keydown", function(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault(); // 기본 줄바꿈 방지
      sendMessage(roomId, userId);
    }
  });
};
</script>
</head>

<body>
<%
  ChatRoom room = (ChatRoom) request.getAttribute("room");
  long roomId = room != null ? room.getId() : 1L; // room이 없을 때 대비
  long userId = 999L; // 로그인 없이 테스트용 ID
%>

<h2>USER ID: <%= roomId %></h2>

<div id="chatBox"></div>

<div>
  <textarea id="msg" placeholder="메시지를 입력하세요 (Shift+Enter 줄바꿈)" ></textarea>
  <button onclick="sendMessage('<%= roomId %>', '<%= userId %>')">보내기</button>
</div>

<!-- 숨겨진 데이터 -->
<input type="hidden" id="roomId" value="<%= roomId %>">
<input type="hidden" id="userId" value="<%= userId %>">
</body>
</html>

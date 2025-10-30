<%@ page import="model.ChatRoom" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>채팅방 (테스트용)</title>
<script>
let ws;

// WebSocket 연결
function connect(roomId, userId) {
    ws = new WebSocket("ws://localhost:8080/yourAppName/chatSocket/" + roomId + "/" + userId);

    ws.onopen = () => {
        console.log(" WebSocket 연결 성공");
    };

    ws.onmessage = (event) => {
        const chatBox = document.getElementById("chatBox");
        const msgDiv = document.createElement("div");
        msgDiv.textContent = event.data;
        chatBox.appendChild(msgDiv);
        chatBox.scrollTop = chatBox.scrollHeight;
    };

    ws.onclose = () => {
        console.log(" WebSocket 연결 종료");
    };

    ws.onerror = (err) => {
        console.error(" WebSocket 오류:", err);
    };
}

function sendMessage(roomId, userId) {
    const input = document.getElementById("msg");
    const msg = input.value.trim();
    if (msg === "") return;

    ws.send(userId + ": " + msg);
    input.value = "";
}

window.onload = function() {
    const roomId = document.getElementById("roomId").value;
    const userId = document.getElementById("userId").value;
    connect(roomId, userId);
};
</script>
</head>
<body>

<%
    ChatRoom room = (ChatRoom) request.getAttribute("room");

    // 테스트용 사용자 ID 
    long userId = 999L;

    // 방 정보
    long roomId = room.getId();
    long productId = room.getProductsId();
%>

<h2>채팅방 (테스트용)</h2>
<p>채팅방 번호: <%= roomId %></p>
<p>상품 번호: <%= productId %></p>
<p>현재 사용자 ID(테스트용): <%= userId %></p>

<div id="chatBox" style="border:1px solid #aaa; width:400px; height:300px; overflow:auto; margin-bottom:10px;"></div>
<input type="text" id="msg" style="width:300px;">
<button onclick="sendMessage('<%= roomId %>', '<%= userId %>')">보내기</button>

<!-- WebSocket 연결용 hidden input -->
<input type="hidden" id="roomId" value="<%= roomId %>">
<input type="hidden" id="userId" value="<%= userId %>">

</body>
</html>

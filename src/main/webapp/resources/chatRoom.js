let ws; // WebSocket 객체

// WebSocket 연결
function connect(roomId, userId) {
  // 프로젝트 context path 자동 인식 (예: /userMarket)
  const contextPath = window.location.pathname.split("/")[1];
  const wsUrl = "ws://" + window.location.host + "/" + contextPath + "/chatSocket/" + roomId + "/" + userId;

  console.log("WebSocket 연결 시도:", wsUrl);

  ws = new WebSocket(wsUrl);

  // 연결 성공
  ws.onopen = () => console.log("WebSocket 연결 성공");

  // 메시지 수신 처리
  ws.onmessage = (event) => {
    const chatBox = document.getElementById("chatBox");
    const text = event.data;
    const userIdVal = document.getElementById("userId").value;
    const isMine = text.startsWith("[" + userIdVal + "]");

    const msgDiv = document.createElement("div");
    msgDiv.className = isMine ? "my-message" : "other-message";
    msgDiv.textContent = text.replace("[" + userIdVal + "] : ", "");

    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
  };

  // 오류/종료 로그
  ws.onerror = (e) => console.error("WebSocket 오류:", e);
  ws.onclose = () => console.log("🔻 WebSocket 연결 종료");
}

// 메시지 전송
function sendMessage(roomId, userId) {
  const input = document.getElementById("msg");
  const msg = input.value.trim();

  if (!ws || ws.readyState !== WebSocket.OPEN) {
    console.error("WebSocket이 아직 연결되지 않았습니다.");
    return;
  }

  if (msg === "") return;

  ws.send(msg);
  input.value = "";
}

// 초기화: 엔터 전송 / Shift+Enter 줄바꿈
window.onload = function() {
  const roomId = document.getElementById("roomId").value;
  const userId = document.getElementById("userId").value;
  connect(roomId, userId);

  const msgInput = document.getElementById("msg");
  const sendBtn = document.getElementById("sendBtn");

  // 엔터 → 메시지 전송
  msgInput.addEventListener("keydown", function(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage(roomId, userId);
    }
  });

  // 버튼 클릭 → 메시지 전송
  sendBtn.addEventListener("click", function() {
    sendMessage(roomId, userId);
  });
};

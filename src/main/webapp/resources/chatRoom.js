let ws;

function connect(roomId, userId) {
  const wsUrl = "ws://" + window.location.host + "/userMarket/chatSocket/" + roomId + "/" + userId;
  console.log("WebSocket 연결 시도:", wsUrl);

  ws = new WebSocket(wsUrl);

  ws.onopen = () => console.log("WebSocket 연결 성공");

  ws.onmessage = (event) => {
    const chatBox = document.getElementById("chatBox");
    const msgDiv = document.createElement("div");
    msgDiv.textContent = event.data;
    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
  };

  ws.onerror = (e) => console.error("WebSocket 오류:", e);
  ws.onclose = () => console.log("연결 종료");
}

function sendMessage(roomId, userId) {
  const input = document.getElementById("msg");
  const msg = input.value.trim();

  if (!ws || ws.readyState !== WebSocket.OPEN) {
    console.error("⚠WebSocket 연결이 아직 열리지 않았습니다.");
    return;
  }

  if (msg === "") return;
  ws.send(msg);
  input.value = "";
}

window.onload = function() {
  const roomId = document.getElementById("roomId").value;
  const userId = document.getElementById("userId").value;
  connect(roomId, userId);

  const msgInput = document.getElementById("msg");
  const sendBtn = document.getElementById("sendBtn");

  msgInput.addEventListener("keydown", function(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage(roomId, userId);
    }
  });

  sendBtn.addEventListener("click", function() {
    sendMessage(roomId, userId);
  });
};

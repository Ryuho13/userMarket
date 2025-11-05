let ws;
let currentUserId;

// 페이지 로드 시 실행될 초기화 함수
window.onload = function() {
  const roomIdInput = document.getElementById("roomId");
  const userIdInput = document.getElementById("userId");

  // roomIdInput 또는 userIdInput이 없으면 함수를 즉시 종료
  if (!roomIdInput || !userIdInput) {
    console.error("채팅방 정보를 찾을 수 없습니다. (roomId 또는 userId 요소가 없음)");
    return;
  }

  const roomId = roomIdInput.value;
  currentUserId = userIdInput.value;
  
  if(roomId && currentUserId) {
    connect(roomId, currentUserId);
  }

  const msgInput = document.getElementById("msg");
  const sendBtn = document.getElementById("sendBtn");

  // 엔터 키로 메시지 전송 (Shift+Enter는 줄바꿈)
  msgInput.addEventListener("keydown", function(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault(); // 기본 동작(줄바꿈) 방지
      sendMessage();
    }
  });

  // 보내기 버튼 클릭으로 메시지 전송
  sendBtn.addEventListener("click", function() {
    sendMessage();
  });
};

// WebSocket 서버에 연결하는 함수
function connect(roomId, userId) {
  const wsUrl = `ws://${window.location.host}/userMarket/chatSocket/${roomId}/${userId}`;
  console.log("WebSocket 연결 시도:", wsUrl);

  ws = new WebSocket(wsUrl);

  ws.onopen = () => console.log("✅ WebSocket 연결 성공");
  ws.onclose = () => console.log("🔌 WebSocket 연결 종료");
  ws.onerror = (e) => console.error("❌ WebSocket 오류:", e);
  
  // 서버로부터 메시지를 수신했을 때 실행될 함수
  ws.onmessage = (event) => {
    try {
      const messageData = JSON.parse(event.data);
      appendMessage(messageData);
    } catch (e) {
      console.error("수신 데이터 파싱 오류:", event.data, e);
    }
  };
}

// 메시지를 전송하는 함수
function sendMessage() {
  const input = document.getElementById("msg");
  const msg = input.value.trim();

  if (!ws || ws.readyState !== WebSocket.OPEN) {
    console.error("⚠ WebSocket이 연결되지 않았습니다.");
    // 필요하다면 여기에 재연결 로직을 추가할 수 있습니다.
    return;
  }

  if (msg === "") return;

  // 서버에 보낼 데이터를 JSON 형식으로 구성
  const payload = {
    message: msg
  };

  ws.send(JSON.stringify(payload));
  input.value = ""; // 입력창 비우기
  input.focus();
}

// 채팅창에 새 메시지를 추가하는 함수
function appendMessage(data) {
  const chatBox = document.getElementById("chatBox");

  const isMine = data.senderId == currentUserId;

  // 1. 가장 바깥쪽 div (chat-row)
  const chatRow = document.createElement("div");
  chatRow.classList.add("chat-row");
  chatRow.classList.add(isMine ? "my-message" : "other-message");

  // 2. 말풍선 div (bubble)
  const bubble = document.createElement("div");
  bubble.classList.add("bubble");

  // 3. 메시지 텍스트 span
  const messageText = document.createElement("span");
  messageText.classList.add("message-text");
  messageText.textContent = data.message;

  // 4. 시간 span
  const time = document.createElement("span");
  time.classList.add("time");
  time.textContent = data.createdAt;

  // 5. 생성한 요소들을 조립 (안쪽부터 바깥쪽으로)
  bubble.appendChild(messageText);
  bubble.appendChild(time);
  chatRow.appendChild(bubble);

  // 6. 완성된 메시지를 채팅창에 추가
  chatBox.appendChild(chatRow);

  // 7. 스크롤을 가장 아래로 내리기
  chatBox.scrollTop = chatBox.scrollHeight;
}